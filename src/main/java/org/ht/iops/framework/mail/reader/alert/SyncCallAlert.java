package org.ht.iops.framework.mail.reader.alert;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.ht.iops.db.repository.StatusRepository;
import org.ht.iops.db.repository.config.AppConfigRepository;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.exception.ApplicationValidationException;
import org.ht.iops.framework.mail.MailData;
import org.ht.iops.framework.mail.reader.BaseMailReader;
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
public class SyncCallAlert extends AlertReader<String> {
	/** Subject prefix string for the Sync call alert. */
	final private static String SUBJECT_PREFIX = "Ecom Splunk Alert | Major | Hourly | ";

	/**
	 * Constructor for <tt>SyncCallReader</tt> derived from the super class
	 * constructor {@link BaseMailReader}. Spring uses this constructor for
	 * injecting dependencies.
	 * 
	 * @param statusRepository
	 *            - Repository for DB table 'Status'
	 * @param eventPublisher
	 *            - Event publisher
	 * @param configRepository
	 *            - Repository for DB table 'AppConfig'
	 */
	public SyncCallAlert(final StatusRepository statusRepository,
			final EventPublisher eventPublisher,
			final AppConfigRepository configRepository) {
		super(statusRepository, eventPublisher, configRepository);
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
	 * @return list containing values extracted from HTML.
	 */
	@Override
	protected List<String> getDetailsFromHTML(final MailData mailData) {
		List<String> details = new ArrayList<>();
		try {
			details.add(mailData.getSubject().replace(SUBJECT_PREFIX, "")
					.replace(" - Sync Call", ""));
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
					"Invalid HTML document recieved in email.",
					getReportName());
		}
		return details;
	}

	/**
	 * Method for applying transformations on the input HTML. Converts alert
	 * specific time format into Java standard format. E.g. Alert format - 'Thu
	 * Apr 28 21:00:00 2016'
	 * 
	 * @param details
	 *            - list containing all the details about the alert.
	 * @throws ParseException
	 */
	@Override
	protected void applyTransformations(final List<String> details)
			throws ParseException {
		details.set(1, getReportTime(details.get(1)));
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
	@Override
	protected IOpsEvent createAlertEvent(final List<String> arguments) {
		Assert.notEmpty(arguments, "Arguments cannot be null.");
		IOpsEvent event = createEvent(getReportName());
		event.setMessageArguments(arguments.toArray(new String[]{}));
		event.addAttributes("type", "alert");
		return event;
	}

	@Override
	protected String getReportName() {
		return "synccall";
	}
}