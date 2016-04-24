package org.ht.iops.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.ht.iops.bootstrap.IOpsApplication;
import org.ht.iops.events.IOpsEmailEvent;
import org.ht.iops.exception.ApplicationRuntimeException;
import org.ht.iops.exception.ApplicationValidationException;
import org.ht.iops.framework.mail.MailData;
import org.ht.iops.rest.response.Jira;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = IOpsApplication.class)
@ActiveProfiles("test")
public class EmailServiceTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Autowired
	private EmailService emailService;
	private GreenMail greenMail;

	public EmailServiceTest() {
		System.setProperty("iops.testmode", "true");
	}

	@Before
	public void setUp() {
		greenMail = new GreenMail(ServerSetupTest.SMTP);
		greenMail.start();
		greenMail.setUser("test@iops.com", "test", "test");
	}

	@After
	public void tearDown() {
		greenMail.stop();
	}

	@Test
	public void sendEmailForEventNullEvent() {
		thrown.expect(ApplicationValidationException.class);
		thrown.expectMessage("Email event cannot be null.");
		emailService.sendEmailForEvent(null);
	}

	@Test
	public void sendEmailForEventNullType() {
		thrown.expect(ApplicationValidationException.class);
		thrown.expectMessage("Event Type cannot be null.");
		IOpsEmailEvent emailEvent = new IOpsEmailEvent("", new MailData(),
				null);
		emailService.sendEmailForEvent(emailEvent);
	}

	@Test
	public void sendEmailForEventNullTokens() {
		thrown.expect(ApplicationValidationException.class);
		thrown.expectMessage("Tokens cannot be null.");
		Map<String, Object> tokens = new HashMap<>();
		IOpsEmailEvent emailEvent = new IOpsEmailEvent("jira", new MailData(),
				tokens);
		emailService.sendEmailForEvent(emailEvent);
	}

	@Test
	public void sendEmailForEventNoTemplate() {
		thrown.expect(ApplicationRuntimeException.class);
		thrown.expectMessage(
				startsWith("Error resolving template \"unknown/success\""));
		Map<String, Object> tokens = new HashMap<>();
		tokens.put("test", "test");
		IOpsEmailEvent emailEvent = new IOpsEmailEvent("unknown",
				new MailData(), tokens);
		emailService.sendEmailForEvent(emailEvent);
	}

	@Test
	public void sendMailNullEmailTO() {
		thrown.expect(ApplicationValidationException.class);
		thrown.expectMessage("Email TO cannot be null.");
		emailService.sendEmail(null, null, null, false);
	}

	@Test
	public void sendMailNullSubject() {
		thrown.expect(ApplicationValidationException.class);
		thrown.expectMessage("Email subject cannot be null.");
		emailService.sendEmail("test@iops.com", "", null, false);
	}

	@Test
	public void sendMailNullMessage() {
		thrown.expect(ApplicationValidationException.class);
		thrown.expectMessage("Email message cannnot be null.");
		emailService.sendEmail("test@iops.com", "Test Subject", "", false);
	}

	@Test
	public void sendMailValidateEmail() throws MessagingException {
		emailService.sendEmail("test@iops.com", "Test Subject",
				"Sending a test email message", false);
		MimeMessage[] messages = greenMail.getReceivedMessages();
		assertThat(messages).isNotEmpty().hasSize(1);
		assertThat(messages[0]).extracting("subject").toString()
				.equals("Test Subject");
	}

	@Test
	public void sendMailValidateEmailREMode() throws MessagingException {
		emailService.sendEmail("test@iops.com", "Test_Subject",
				"Sending a test email message", true);
		MimeMessage[] messages = greenMail.getReceivedMessages();
		assertThat(messages).isNotEmpty().hasSize(1);
		assertThat(messages[0]).extracting("subject").toString()
				.equals("RE: Test_Subject");
	}

	@Test
	public void sendMailFailure() throws MessagingException {
		tearDown();
		thrown.expect(ApplicationRuntimeException.class);
		try {
			emailService.sendEmail("test@iops.com", "Test Subject",
					"Sending a test email message", true);
		} finally {
			setUp();
		}
	}

	@Test
	public void sendEmailForEvent() throws MessagingException {
		Map<String, Object> tokens = new HashMap<>();
		tokens.put("summary", "Test_Summary");
		tokens.put("issues", setupJiraIssues());
		IOpsEmailEvent emailEvent = new IOpsEmailEvent("jira", setupMailData(),
				tokens);
		emailService.sendEmailForEvent(emailEvent);
		MimeMessage[] messages = greenMail.getReceivedMessages();
		assertThat(messages).isNotEmpty().hasSize(1);
		assertThat(messages[0]).extracting("subject").toString()
				.equals("RE: Test_Subject");
	}

	private List<Jira> setupJiraIssues() {
		Jira jira = new Jira();
		jira.setKey("TEST-101");
		jira.setUrl("http://localhost/TEST-101");
		List<Jira> issues = new ArrayList<>();
		issues.add(jira);
		return issues;
	}

	private MailData setupMailData() {
		MailData mailData = new MailData();
		mailData.setSubject("Test_Subject");
		mailData.setMessageFrom("test@iops.com");
		return mailData;
	}
}
