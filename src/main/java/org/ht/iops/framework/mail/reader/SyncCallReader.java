package org.ht.iops.framework.mail.reader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.mail.internet.MimeMessage;

import org.ht.iops.db.beans.Status;
import org.ht.iops.db.repository.StatusRepository;
import org.ht.iops.db.repository.config.AppConfigRepository;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.exception.ApplicationValidationException;
import org.ht.iops.framework.mail.MailData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Email reader class for reading Splunk sync call failure reports. Email
 * received will be validated against the rule engine and an application event
 * will be raised based on the rules. Event listeners will then take appropriate
 * actions like sending notifications to Slack or other third party
 * integrations.
 * 
 * @author Himanshu Tomar
 * @since 1.0
 *
 */
@Component
public class SyncCallReader extends BaseMailReader {
	/** Subject prefix string for the Sync call alert. */
	final private static String SUBJECT_PREFIX = "Ecom Splunk Alert | Major | Hourly | ";
	/** Logger */
	final private static Logger LOGGER = LoggerFactory
			.getLogger(SyncCallReader.class);

	/**
	 * Constructor for <tt>SyncCallReader</tt> derived from the super class
	 * constructor {@link BaseMailReader}. Spring uses this constructor for
	 * injecting dependencies.
	 * 
	 * @param mimeMessageReader
	 *            - mine message parser instance
	 * @param statusRepository
	 *            - Repository for DB table 'Status'
	 * @param appConfigRepository
	 *            - Repository for DB table 'Status'
	 * @param eventPublisher
	 *            - Event publisher
	 */
	public SyncCallReader(final MimeMessageReader mimeMessageReader,
			final StatusRepository statusRepository,
			final AppConfigRepository appConfigRepository,
			final EventPublisher eventPublisher) {
		super(mimeMessageReader, statusRepository, appConfigRepository,
				eventPublisher);
	}

	@Override
	protected Status postProcess(final MimeMessage message,
			final MailData mailData) {
		Status status = null;
		final String reportName = mailData.getSubject().replace(SUBJECT_PREFIX,
				"");
		if (null == mailData.getHtmlDocument()) {
			throw new ApplicationValidationException(
					"Invalid message body contents, no HTML document",
					reportName);
		}
		LOGGER.debug("HTML message" + mailData.getHtmlContent());
		try {
			List<String> details = getDetailsFromHTML(mailData, reportName);
			convertTime(details, reportName);
			eventPublisher.createEvent(createSyncCallEvent(details));
		} catch (ParseException exception) {
			throw new ApplicationValidationException(exception.getMessage(),
					reportName);
		}
		return status;
	}

	/**
	 * Method for parsing HTML body of the Splunk Sync Call alert. This method
	 * will parse and extract following information from the HTML:
	 * <ul>
	 * <li>Time of the alert</li>
	 * <li>Total requests</li>
	 * <li>Failed requests</li>
	 * <li>Failure percentage</li>
	 * </ul>
	 * 
	 * @param mailData
	 *            - mail data object containing email details
	 * @param reportName
	 *            - name of the Sync Call (like 'Get Order List')
	 * @return list containing values extracted from HTML.
	 */
	protected List<String> getDetailsFromHTML(final MailData mailData,
			final String reportName) {
		List<String> details = new ArrayList<>();
		try {
			details.add(reportName);
			mailData.getHtmlDocument().getElementsByClass("results").first()
					.childNodes().stream()
					.forEach(tBody -> tBody.childNodes().stream()
							.filter(trElements -> trElements.toString()
									.contains("valign"))
							.forEach(trElements -> trElements.childNodes()
									.stream()
									.filter(tdElement -> "td"
											.equals(tdElement.nodeName()))
									.forEach(tdElement -> tdElement.childNodes()
											.forEach(preElement -> details
													.add(preElement.childNode(0)
															.toString())))));
		} catch (NullPointerException
				| IllegalArgumentException illegalArgumentException) {
			throw new ApplicationValidationException(
					"Invalid HTML document recieved in email.", reportName);
		}
		return details;
	}

	/**
	 * Converts alert specific time format into Java standard format. E.g. Alert
	 * format - 'Thu Apr 28 21:00:00 2016'
	 * 
	 * @param details
	 * @param reportName
	 * @throws ParseException
	 */
	protected void convertTime(final List<String> details,
			final String reportName) throws ParseException {
		details.set(1, getReportTime(details.get(1), reportName));
	}

	/**
	 * Converts alert specific time format into Java standard format. E.g. Alert
	 * format - 'Thu Apr 28 21:00:00 2016'. Reads the alert format from
	 * 'AppConfig' using repositories.
	 * 
	 * @param reportTime
	 * @param reportName
	 * @return
	 * @throws ParseException
	 */
	protected String getReportTime(final String reportTime,
			final String reportName) throws ParseException {
		Assert.hasText(reportTime,
				"Null or empty date passed for: " + reportName);
		DateFormat dateFormat = new SimpleDateFormat(appConfigRepository
				.findByNameAndType(getReportName(), "dateformat").getValue(),
				Locale.ENGLISH);
		return dateFormat.parse(reportTime).toString();
	}

	/**
	 * This method creates an event of type 'synccall' which is then raised
	 * using {@link EventPublisher}. Converts the parsed HTML tokens into
	 * String[] and populates event with the same.
	 * 
	 * @param arguments
	 *            - token from parse HTML message.
	 * @return create event
	 */
	protected IOpsEvent createSyncCallEvent(final List<String> arguments) {
		Assert.notEmpty(arguments, "Arguments cannot be null.");
		IOpsEvent event = createEvent(getReportName());
		arguments.add(System.lineSeparator());
		event.setMessageArguments(arguments.toArray(new String[]{}));
		event.addAttributes("type", "alert");
		return event;
	}

	@Override
	protected boolean requireHTMLElements() {
		return true;
	}

	@Override
	protected String getReportName() {
		return "synccall";
	}
}