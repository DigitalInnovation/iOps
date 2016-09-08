package org.ht.iops.framework.mail.reader.alert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.ht.iops.db.repository.StatusRepository;
import org.ht.iops.db.repository.config.AppConfigRepository;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.exception.ApplicationValidationException;
import org.ht.iops.framework.mail.MailData;
import org.ht.iops.framework.mail.reader.BaseMailReader;
import org.ht.iops.rest.request.Attachment;
import org.ht.iops.rest.request.Fields;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PlatformAlert extends AlertReader<Attachment> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PlatformAlert.class);

	private static final Pattern PLATFORM_ALERT_DATEPATTERN = Pattern
			.compile("(\\d{1,2}/\\d{1,2}/\\d{2})");

	private static final Pattern CTRLM_ALERT_DATEPATTERN = Pattern
			.compile("(\\d{14})");

	private static final String[] DATE_FORMATS = {"yyyyMMddHHmmss"};

	private static final String[] CTRLM_ALERTS_HEADERS = {"Job", "Node",
			"Status", "ExecutionTime"};

	private static final String[] PLATFORM_ALERTS_HEADERS = {"Alert",
			"Environment", "Status"};

	/**
	 * Constructor for <tt>PlatformAlert</tt> derived from the super class
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
	public PlatformAlert(final StatusRepository statusRepository,
			final EventPublisher eventPublisher,
			final AppConfigRepository configRepository) {
		super(statusRepository, eventPublisher, configRepository);
	}

	@Override
	protected List<Attachment> getDetailsFromHTML(MailData mailData) {
		List<Map<String, String>> details = new ArrayList<>();
		LOGGER.trace("getDetailsFromHTML processing HTML");
		try {
			mailData.getHtmlDocument().getElementsByTag("table").first()
					.childNodes().stream()
					.filter(tableChilds -> tableChilds.nodeName()
							.contains("tbody"))
					.forEach(tBody -> tBody.childNodes().stream()
							.filter(tbodyChilds -> tbodyChilds.nodeName()
									.contains("tr"))
							.forEach(trElement -> parseHTML(trElement,
									details)));
			details.remove(0);
		} catch (NullPointerException
				| IllegalArgumentException illegalArgumentException) {
			throw new ApplicationValidationException(
					"Invalid HTML document recieved in email.",
					getReportName());
		}
		LOGGER.trace("getDetailsFromHTML parsed HTML: details=" + details);
		return applyInternalTransformations(details);
	}

	protected void parseHTML(final Node trElement,
			final List<Map<String, String>> details) {
		Map<String, String> data = new HashMap<>();
		List<Node> trueTR = trElement.childNodes().stream()
				.filter(trChild -> trChild.nodeName().contains("td")
						|| trChild.nodeName().contains("th"))
				.collect(Collectors.toList());
		data.put("Asset", trueTR.get(0).childNode(0).toString());
		data.put("Severity", trueTR.get(1).childNode(0).toString());
		data.put("Mode", trueTR.get(2).childNode(0).toString());
		data.put("Message", trueTR.get(3).childNode(0).toString());
		details.add(data);
	}

	@Override
	protected void publishEvent(IOpsEvent event) {
		if (null != event)
			super.publishEvent(event);
	}

	@Override
	protected IOpsEvent createAlertEvent(List<Attachment> attachments) {
		IOpsEvent event = null;
		if (null != attachments && !attachments.isEmpty()) {
			LOGGER.debug("Creating event type=alert, report=" + getReportName()
					+ " attachment=" + attachments);
			event = new IOpsEvent(getReportName(), null);
			event.addAttributes("type", "alert");
			event.setResponseRequired(false);
			event.setAttachments(attachments);
		}
		return event;
	}

	protected List<Attachment> applyInternalTransformations(
			List<Map<String, String>> details) {
		List<Attachment> attachments = new ArrayList<>();
		LOGGER.trace("applyInternalTransformations Details: " + details);
		details.stream()
				.filter(alert -> "major".equalsIgnoreCase(alert.get("Severity"))
						&& "keep".equalsIgnoreCase(alert.get("Mode")))
				.forEach(alert -> processAlert(alert, attachments));
		LOGGER.debug("Attachments : " + attachments);
		return attachments;
	}

	private void processAlert(Map<String, String> alert,
			final List<Attachment> attachments) {
		String message = alert.get("Message");
		if (message.contains("PLT_EVT")) {
			LOGGER.debug("Processing AMS alerts");
			processAMSAlerts(message, attachments);
		} else {
			LOGGER.debug("Processing CTRLM alerts");
			processCTRLAlerts(message, attachments);
		}
	}

	private void processCTRLAlerts(final String message,
			final List<Attachment> attachments) {
		String jobName = StringUtils.tokenizeToStringArray(
				message.substring(message.indexOf("The JOB PROD_"),
						message.length() - 1).replaceAll("The JOB PROD_", ""),
				" ")[0];
		String nodeName = StringUtils.tokenizeToStringArray(
				message.substring(message.indexOf("ran on node "),
						message.length() - 1).replaceAll("ran on node", ""),
				" ")[0];
		String status = message.contains("Job status-") ? "Delays" : "Failed";
		LOGGER.debug("Adding CTRLM attachment - Job: " + jobName + " Node: "
				+ nodeName + " Status: " + status);
		addAttachments(attachments, CTRLM_ALERTS_HEADERS, jobName, nodeName,
				status, getCTRLMTimeStamp(message));
	}

	private void processAMSAlerts(final String message,
			final List<Attachment> attachments) {
		String[] tokens = StringUtils.delimitedListToStringArray(message, "||");
		String[] eventName = StringUtils.tokenizeToStringArray(tokens[2], " ");
		String summary = getSummary(tokens[6]);
		LOGGER.debug("Adding AMS attachment - eventName[1]: " + eventName[1]
				+ " eventName[0]: " + eventName[0] + " summary: " + summary);
		addAttachments(attachments, PLATFORM_ALERTS_HEADERS, eventName[1],
				eventName[0], summary);
	}

	private String getSummary(final String alertText) {
		Matcher matcher = PLATFORM_ALERT_DATEPATTERN.matcher(alertText);
		String dateString = "";
		while (matcher.find()) {
			dateString = matcher.group(1);
		}

		int startIndex = alertText.indexOf("[" + dateString);
		int endIndex = alertText.indexOf("regex:\\");

		LOGGER.trace("getSummary(String) alerText: " + alertText);
		LOGGER.debug("getSummary(String) DateString: " + dateString
				+ " startIndex: " + startIndex + " endIndex: " + endIndex);

		return startIndex > -1 && endIndex > -1 && endIndex > startIndex
				? alertText.substring(startIndex, endIndex)
				: alertText;
	}

	private String getCTRLMTimeStamp(final String alertText) {
		String timeStamp = "";
		Matcher matcher = CTRLM_ALERT_DATEPATTERN.matcher(alertText);
		String dateString = "";
		while (matcher.find()) {
			dateString = matcher.group(1);
		}
		try {
			Date alertTimeStamp = new SimpleDateFormat(DATE_FORMATS[0])
					.parse(dateString);
			timeStamp = alertTimeStamp.toString();
			LOGGER.trace("getCTRLMTimeStamp(String) dateString: " + dateString
					+ " parsed timeStamp: " + timeStamp);
		} catch (ParseException exception) {
			LOGGER.error("getCTRLMTimeStamp error occured while parsing date :"
					+ dateString);
		}

		return timeStamp;
	}

	private void addAttachments(final List<Attachment> attachments,
			final String[] headers, final String... alertDetails) {
		Attachment attachment = new Attachment();
		attachment.addField(new Fields(headers[0], alertDetails[0], true));
		attachment.addField(new Fields(headers[1], alertDetails[1], true));

		if (alertDetails.length > 3) {
			attachment.addField(new Fields(headers[2], alertDetails[2], true));
			attachment.addField(new Fields(headers[3], alertDetails[3], true));
		} else {
			attachment.addField(new Fields(headers[2], alertDetails[2], false));
		}

		attachment.setColor("#F35A00");
		attachments.add(attachment);
	}

	@Override
	protected String getReportName() {
		return "platformalert";
	}

	@Override
	protected void applyTransformations(List<Attachment> details)
			throws ParseException {
	}
}