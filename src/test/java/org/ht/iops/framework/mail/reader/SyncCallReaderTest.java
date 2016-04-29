package org.ht.iops.framework.mail.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ht.iops.db.beans.SlackConfig;
import org.ht.iops.db.beans.config.AppConfig;
import org.ht.iops.db.repository.SlackConfigRepository;
import org.ht.iops.db.repository.config.AppConfigRepository;
import org.ht.iops.events.adapters.SlackAdapter;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.exception.ApplicationException;
import org.ht.iops.exception.ApplicationValidationException;
import org.ht.iops.framework.mail.MailData;
import org.ht.iops.rest.request.SlackRequest;
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
public class SyncCallReaderTest {
	private String DATE_FORMAT = "EEE MMM dd HH:mm:ss yyyy";
	private static Document successHTML = null;
	private static Document errorHTML = null;

	private SyncCallReader syncCallReader;
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
		InputStream successHTMLStream = new ClassPathResource(
				"message/templates/SyncCall.html").getInputStream();
		InputStream errorHTMLStream = new ClassPathResource(
				"message/templates/SyncCallError.html").getInputStream();
		successHTML = Jsoup.parse(successHTMLStream, "UTF-8", "");
		errorHTML = Jsoup.parse(errorHTMLStream, "UTF-8", "");
		successHTMLStream.close();
		errorHTMLStream.close();
	}

	@Before
	public void setUp() {
		syncCallReader = new SyncCallReader(new MimeMessageReader(), null,
				appConfigRepository,
				new EventPublisher(applicationEventPublisher));
		slackAdapter = new SlackAdapter(slackConfigRepository);
		setUpAppConfig();
		when(mailData.getSubject()).thenReturn(
				"Ecom Splunk Alert | Major | Hourly | Get Order List - Sync Call");
	}

	private void setUpAppConfig() {
		AppConfig dateFormat = createAppConfig(syncCallReader.getReportName(),
				"dateformat", DATE_FORMAT);
		when(appConfigRepository.findByNameAndType(
				syncCallReader.getReportName(), "dateFormat"))
						.thenReturn(dateFormat);
	}

	private void setUpSlackConfig() {
		SlackConfig config = new SlackConfig();
		config.setName(syncCallReader.getReportName());
		config.setListner("test");
		config.setType("alert");
		config.setText(
				"*_`Major Alert`_* -> Sync call -> {0} -> Error percentage > 5%{5}Error percentage *{4}%* ({3} out of {2})");
		when(slackConfigRepository.findByNameAndListnerAndType(
				syncCallReader.getReportName(), "test", "alert"))
						.thenReturn(config);
	}

	@Test
	public void requireHTMLElements_Validate() {
		assertThat(syncCallReader.requireHTMLElements()).isEqualTo(true);
	}

	@Test
	public void getReportTime_ValidatFormat_ShouldParseWithoutException()
			throws ParseException {
		Date date = new Date();
		assertThat(syncCallReader.getReportTime(getSynCallDate(date),
				getReportName())).isNotNull().isEqualTo(date.toString());
	}

	@Test
	public void getReportTime_NullDate_ShouldThrowIllegalArgumentException()
			throws ParseException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(
				"Null or empty date passed for: " + getReportName());
		assertThat(syncCallReader.getReportTime(null, getReportName()));
	}

	@Test
	public void getReportTime_InvalidDate_ShouldThrowParseException()
			throws ParseException {
		thrown.expect(ParseException.class);
		assertThat(syncCallReader.getReportTime("Apr Thu 28 21:00:00 2016 GMT",
				getReportName()));
	}

	@Test
	public void convertTime_ValidDate_ShouldModifyList() throws ParseException {
		List<String> details = new ArrayList<>();
		details.add("");
		details.add("Thu Apr 28 21:00:00 2016");
		syncCallReader.convertTime(details, getReportName());
		assertThat(details.get(1)).isEqualTo("Thu Apr 28 21:00:00 BST 2016");
	}

	@Test
	public void postProcess_NullDocument_ShouldThrowAppValException()
			throws ApplicationException {
		thrown.expect(ApplicationValidationException.class);
		thrown.expectMessage("Invalid message body contents, no HTML document");
		when(mailData.getHtmlDocument()).thenReturn(null);
		syncCallReader.postProcess(null, mailData);
	}

	@Test
	public void getDetailsFromHTML_ValidDocument_ShouldReturnExpectedListOfString() {
		List<String> expectedList = createExpectedList();
		setHTMLDocument(successHTML);
		assertThat(syncCallReader.getDetailsFromHTML(mailData, getReportName()))
				.isNotEmpty().hasSameSizeAs(expectedList)
				.hasSameElementsAs(expectedList);
	}

	@Test
	public void getDetailsFromHTML_InValidDocument_ShouldThrowAppValException() {
		thrown.expect(ApplicationValidationException.class);
		thrown.expectMessage("Invalid HTML document recieved in email.");
		setHTMLDocument("<html><body></body></html>");
		syncCallReader.getDetailsFromHTML(mailData, getReportName());
	}

	@Test
	public void postProcess_ValidDocument_ShouldRunSuccessfully() {
		setHTMLDocument(successHTML);
		syncCallReader.postProcess(null, mailData);
	}

	@Test
	public void postProcess_InValidDateFormat_ShouldThrowAppValException() {
		thrown.expect(ApplicationValidationException.class);
		setHTMLDocument(errorHTML);
		syncCallReader.postProcess(null, mailData);
	}

	@Test
	public void createSyncCallEvent_NullList_ShouldThrowIllegalArgumentException() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Arguments cannot be null.");
		syncCallReader.createSyncCallEvent(null);
	}

	@Test
	public void createSyncCallEvent_ValidDetails() {
		assertThat(syncCallReader.createSyncCallEvent(createExpectedList()))
				.extracting("messageArguments").isNotNull();
	}

	@Test
	public void transformRequest_SlackAdapter_ValidateFormattedString() {
		setUpSlackConfig();
		SlackRequest request = slackAdapter.transformRequest(
				syncCallReader.createSyncCallEvent(createExpectedList()),
				"test");
		assertThat(request).isNotNull();
		assertThat(request.getText()).as("Text").isEqualTo(
				"*_`Major Alert`_* -> Sync call -> Get Order List -> Error percentage > 5%"
						+ System.lineSeparator()
						+ "Error percentage *6.59%* (110 out of 1669)");
	}

	private List<String> createExpectedList() {
		List<String> expectedList = new ArrayList<>();
		expectedList.add(getReportName());
		expectedList.add("Thu Apr 28 21:00:00 2016");
		expectedList.add("1669");
		expectedList.add("110");
		expectedList.add("6.59");
		return expectedList;
	}

	private AppConfig createAppConfig(final String type, final String name,
			final String value) {
		AppConfig appConfig = new AppConfig();
		appConfig.setType(type);
		appConfig.setName(name);
		appConfig.setValue(value);
		return appConfig;
	}

	private String getSynCallDate(final Date date) {
		return (new SimpleDateFormat(DATE_FORMAT)).format(date);
	}

	private String getReportName() {
		return "Get Order List";
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
