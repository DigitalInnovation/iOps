package org.ht.iops.framework.mail.reader.alert;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.ht.iops.framework.mail.reader.BaseMailReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Email reader class for reading all kinds of alerts. Email received will be
 * validated against the rule engine and an application event will be raised
 * based on the rules. Event listeners will then take appropriate actions like
 * sending notifications to Slack or other third party integrations.
 * 
 * @author Himanshu Tomar
 * @since 1.0
 *
 */
public abstract class AlertReader<T> extends BaseMailReader {
	/** Logger */
	final private static Logger LOGGER = LoggerFactory
			.getLogger(AlertReader.class);
	/** Date format for Alert reader. */
	public final static SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat(
			"dd MMM HH:mm");

	/** App config repository instance for getting application configs. */
	final AppConfigRepository configRepository;

	/**
	 * Constructor for <tt>AlertReader</tt> derived from the super class
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
	public AlertReader(final StatusRepository statusRepository,
			final EventPublisher eventPublisher,
			final AppConfigRepository configRepository) {
		super(statusRepository, eventPublisher);
		this.configRepository = configRepository;
	}

	@Override
	protected Status postProcess(final MimeMessage message,
			final MailData mailData) {
		Status status = null;
		if (null == mailData.getHtmlDocument()) {
			throw new ApplicationValidationException(
					"Invalid message body contents, no HTML document",
					getReportName());
		}
		LOGGER.debug("HTML message" + mailData.getHtmlContent());
		try {
			// TODO use the basemailreader parsebody method
			List<T> details = getDetailsFromHTML(mailData);
			applyTransformations(details);
			publishEvent(createAlertEvent(details));
		} catch (ParseException exception) {
			throw new ApplicationValidationException(exception.getMessage(),
					getReportName());
		}
		return status;
	}

	/**
	 * Method for parsing HTML body of the alert. This method needs to be
	 * overridden by the child classes and provide actual implementation of
	 * extracting details from input HTML.
	 * 
	 * @param mailData
	 *            - mail data object containing email details
	 * @return list containing values extracted from HTML.
	 */
	protected abstract List<T> getDetailsFromHTML(final MailData mailData);

	/**
	 * Method for creating event for the alert. This method needs to be
	 * overriden by the child classes and provide actual implementation for
	 * creating event.
	 * 
	 * @param arguments
	 *            - token from parse HTML message.
	 * @return create event
	 */
	protected abstract IOpsEvent createAlertEvent(final List<T> arguments);

	/**
	 * Method for applying transformations on the input HTML. This method needs
	 * to be overridden by the child classes and provide actual implementation
	 * of transformations required (if any).
	 * 
	 * @param details
	 *            - list containing all the details about the alert.
	 * @throws ParseException
	 *             - in case exception occurs while parsing input.
	 */
	protected abstract void applyTransformations(final List<T> details)
			throws ParseException;

	/**
	 * Converts alert specific time format into Java standard format. E.g. Alert
	 * format - 'Thu Apr 28 21:00:00 2016'. Reads the alert format from
	 * 'AppConfig' using repositories.
	 * 
	 * @param reportTime
	 *            - input time
	 * @return formatted date
	 * @throws ParseException
	 *             - in case exeception occurs while parsing the date
	 */
	protected String getReportTime(final String reportTime)
			throws ParseException {
		Assert.hasText(reportTime,
				"Null or empty date passed for " + getReportName());
		DateFormat dateFormat = new SimpleDateFormat(configRepository
				.findByNameAndType(getReportName(), "dateformat").getValue(),
				Locale.ENGLISH);
		return DISPLAY_FORMAT.format(dateFormat.parse(reportTime));
	}

	/**
	 * Method for indicating that this reader will be requiring HTML parsing.
	 */
	@Override
	protected boolean requireHTMLElements() {
		return true;
	}
}
