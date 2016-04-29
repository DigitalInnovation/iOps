package org.ht.iops.framework.mail.reader.instruction;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ht.iops.events.IOpsEvent;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.exception.ApplicationValidationException;
import org.ht.iops.framework.mail.reader.MimeMessageReader;
import org.ht.iops.service.EmailServiceTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class JiraInstructionsTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private JiraInstructions jiraInstructions;

	@Before
	public void setUp() {
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.setServletContext(new MockServletContext());
		jiraInstructions = new JiraInstructions(new MimeMessageReader(), null,
				null, new EventPublisher(applicationContext));
	}

	@Test
	public void createEvent_NullBodyTokens_ShouldThrowAppValException() {
		thrown.expect(ApplicationValidationException.class);
		thrown.expectMessage("Jira description and owner is a required field.");
		jiraInstructions.createEvent(new HashMap<String, String>(),
				new ArrayList<String>(), EmailServiceTest.setupMailData());
	}

	@Test
	public void createEvent_NullOwnerTokens_ShouldThrowAppValException() {
		thrown.expect(ApplicationValidationException.class);
		thrown.expectMessage("Jira description and owner is a required field.");
		Map<String, String> bodyTokens = createBodyTokens();
		bodyTokens.remove("owner");
		jiraInstructions.createEvent(bodyTokens, new ArrayList<String>(),
				EmailServiceTest.setupMailData());
	}

	@Test
	public void createEvent_NullSubjectTokens_ShouldThrowAppValException() {
		thrown.expect(ApplicationValidationException.class);
		thrown.expectMessage("Jira summary is a required field.");
		jiraInstructions.createEvent(createBodyTokens(),
				new ArrayList<String>(), EmailServiceTest.setupMailData());
	}

	@Test
	public void createEvent_EmptySubjectTokens_ShouldThrowAppValException() {
		thrown.expect(ApplicationValidationException.class);
		thrown.expectMessage("Jira summary is a required field.");
		List<String> subjectTokens = createSubjectTokens();
		subjectTokens.set(2, "");
		jiraInstructions.createEvent(createBodyTokens(), subjectTokens,
				EmailServiceTest.setupMailData());
	}

	@Test
	public void createEvent_NullMailData_ShouldThrowIllegalArgumentException() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Mail Data cannot be null.");
		jiraInstructions.createEvent(createBodyTokens(), createSubjectTokens(),
				null);
	}

	@Test
	public void createEvent_Success() {
		IOpsEvent opsEvent = jiraInstructions.createEvent(createBodyTokens(),
				createSubjectTokens(), EmailServiceTest.setupMailData());
		assertThat(opsEvent).isNotNull();
		assertThat(opsEvent.getType()).isEqualTo("jira");
		assertThat(opsEvent).extracting("attributes").isNotEmpty();
		assertThat(opsEvent).isNotNull().extracting("mailData")
				.extracting("subject").isNotEmpty();
	}

	public static Map<String, String> createBodyTokens() {
		Map<String, String> bodyTokens = new HashMap<>();
		bodyTokens.put("description", "Test_Description");
		bodyTokens.put("owner", "Test_Owner");
		return bodyTokens;
	}

	public static List<String> createSubjectTokens() {
		List<String> subjectTokens = new ArrayList<>();
		subjectTokens.add("iOps");
		subjectTokens.add("jira");
		subjectTokens.add("Test_Summary");
		return subjectTokens;
	}
}