package org.ht.iops.framework.mail.reader.alert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.ht.iops.db.beans.SlackConfig;
import org.ht.iops.db.beans.config.AppConfig;
import org.ht.iops.db.repository.SlackConfigRepository;
import org.ht.iops.db.repository.config.AppConfigRepository;
import org.ht.iops.events.adapters.SlackAdapter;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.framework.mail.MailData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class PlatformAlertTest {
	private String DATE_FORMAT = "yyyyMMdd HHmmss";
	private static Document platformAlert = null;
	private static Document ctrlMAlert = null;

	private PlatformAlert alert;
	private SlackAdapter slackAdapter;

	@Mock
	private MailData mailData;
	@Mock
	private AppConfigRepository appConfigRepository;
	@Mock
	private SlackConfigRepository slackConfigRepository;
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@BeforeClass
	public static void classSetup() throws IOException {
		InputStream platformAlertHTMLStream = new ClassPathResource(
				"message/templates/alert_1.html").getInputStream();
		InputStream ctrlMAlertHTMLStream = new ClassPathResource(
				"message/templates/alert_2.html").getInputStream();
		platformAlert = Jsoup.parse(platformAlertHTMLStream, "UTF-8", "");
		ctrlMAlert = Jsoup.parse(ctrlMAlertHTMLStream, "UTF-8", "");
		platformAlertHTMLStream.close();
		ctrlMAlertHTMLStream.close();
	}

	@Before
	public void setUp() {
		alert = new PlatformAlert(null,
				new EventPublisher(applicationEventPublisher),
				appConfigRepository);
		slackAdapter = new SlackAdapter(slackConfigRepository);
		setUpAppConfig();
		when(mailData.getSubject()).thenReturn("MNS PLATFORM Alert");
	}

	private void setUpAppConfig() {
		AppConfig dateFormat = createAppConfig(alert.getReportName(),
				"dateformat", DATE_FORMAT);
		when(appConfigRepository.findByNameAndType(alert.getReportName(),
				"dateformat")).thenReturn(dateFormat);
	}

	private void setUpSlackConfig() {
		SlackConfig config = new SlackConfig();
		config.setName(alert.getReportName());
		config.setListner("test");
		config.setType("alert");
		config.setText(
				"*_`Major Alert`_* -> Platform Alert -> {1}, {2} at {0} -> {4})");
		when(slackConfigRepository.findByNameAndListnerAndType(
				alert.getReportName(), "test", "alert")).thenReturn(config);
	}

	@Test
	public void requireHTMLElements_Validate() {
		assertThat(alert.requireHTMLElements()).isEqualTo(true);
	}

	@Test
	public void postProcess_platformAlertHTML() {
		setHTMLDocument(platformAlert);
		alert.postProcess(null, mailData);
	}

	private AppConfig createAppConfig(final String type, final String name,
			final String value) {
		AppConfig appConfig = new AppConfig();
		appConfig.setType(type);
		appConfig.setName(name);
		appConfig.setValue(value);
		return appConfig;
	}

	private String getReportName() {
		return "platformalert";
	}

	private Document createHTMLDocument(final String htmlText) {
		return Jsoup.parse(htmlText);
	}

	private void setHTMLDocument(final String htmlText) {
		when(mailData.getHtmlDocument())
				.thenReturn(createHTMLDocument(htmlText));
	}

	private void setHTMLDocument(final Document htmlDocument) {
		when(mailData.getHtmlDocument()).thenReturn(htmlDocument);
	}
}
