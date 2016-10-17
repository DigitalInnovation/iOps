package org.ht.iops.framework.mail.reader.alert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.ht.iops.db.beans.JobConfig;
import org.ht.iops.db.repository.JobConfigRepository;
import org.ht.iops.db.repository.StatusRepository;
import org.ht.iops.db.repository.config.AppConfigRepository;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.exception.ApplicationValidationException;
import org.ht.iops.framework.mail.MailData;
import org.ht.iops.framework.mail.reader.BaseMailReader;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PlatformAlert extends AlertReader<List<String>> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PlatformAlert.class);

	private static final Pattern PLATFORM_ALERT_DATEPATTERN = Pattern
			.compile("(\\d{1,2}/\\d{1,2}/\\d{2})");

	private static final Pattern CTRLM_ALERT_DATEPATTERN = Pattern
			.compile("(\\d{14})");

	private static final String[] DATE_FORMATS = {"yyyyMMddHHmmss"};

	private static final String CTRLM_ALERTS = "CTRL-M";

	private static final String PLATFORM_ALERTS = "PLATFORM";

	private JobConfigRepository jobConfigRepository;

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
			final AppConfigRepository configRepository,
			final JobConfigRepository jobConfigRepository) {
		super(statusRepository, eventPublisher, configRepository);
		this.jobConfigRepository = jobConfigRepository;
	}

	@Override
	protected List<List<String>> getDetailsFromHTML(MailData mailData) {
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
	protected List<IOpsEvent> createAlertEvent(
			List<List<String>> eventDetails) {
		List<IOpsEvent> events = new ArrayList<>();
		if (null != eventDetails && !eventDetails.isEmpty()) {
			eventDetails.stream()
					.filter(event -> null != event && !event.isEmpty())
					.forEach(event -> createAlertEvent(events, event));;
		}
		return events;
	}

	private void createAlertEvent(final List<IOpsEvent> events,
			final List<String> messageArguments) {
		String alertType = messageArguments.get(0).toLowerCase()
				.concat("alert");
		LOGGER.debug("Creating event type=alert, report=" + alertType
				+ " messageArguments=" + messageArguments);
		IOpsEvent event = new IOpsEvent(alertType, null);
		event.addAttributes("type", "alert");
		event.setResponseRequired(false);
		event.setMessageArguments(messageArguments.toArray(new String[]{}));
		events.add(event);
	}

	protected List<List<String>> applyInternalTransformations(
			final List<Map<String, String>> details) {
		List<List<String>> alertDetails = new ArrayList<>();
		LOGGER.trace("applyInternalTransformations Details: " + details);
		details.stream()
				.filter(alert -> "major".equalsIgnoreCase(alert.get("Severity"))
						&& "keep".equalsIgnoreCase(alert.get("Mode")))
				.forEach(alert -> processAlert(alert, alertDetails));
		LOGGER.debug("alertDetails : " + alertDetails);
		return alertDetails;
	}

	private void processAlert(final Map<String, String> alert,
			final List<List<String>> alertDetails) {
		String message = alert.get("Message");
		if (message.contains("PLT_EVT")) {
			LOGGER.debug("Processing AMS alerts");
			processAMSAlerts(message, alertDetails);
		} else {
			LOGGER.debug("Processing CTRLM alerts");
			processCTRLAlerts(message, alertDetails);
		}
	}

	private void processCTRLAlerts(final String message,
			final List<List<String>> alertDetails) {
		List<String> alertDetail = new ArrayList<>();
		String jobId = StringUtils.tokenizeToStringArray(
				message.substring(message.indexOf("The JOB PROD_"),
						message.length() - 1).replaceAll("The JOB PROD_", ""),
				" ")[0];
		String nodeName = StringUtils.tokenizeToStringArray(
				message.substring(message.indexOf("ran on node "),
						message.length() - 1).replaceAll("ran on node", ""),
				" ")[0];
		String jobName = getJobName(jobId);
		String status = message.contains("Job status-") ? "Delays" : "Failed";
		LOGGER.debug("Found CTRLM alert with details - JobId: " + jobId
				+ " JobName: " + jobName + " Node: " + nodeName + " Status: "
				+ status);
		alertDetail.addAll(Arrays.asList(new String[]{CTRLM_ALERTS, jobId,
				jobName, nodeName, status, getCTRLMTimeStamp(message)}));
		alertDetails.add(alertDetail);
	}

	private String getJobName(final String jobId) {
		String jobName = "";
		JobConfig config = jobConfigRepository.findByJobId(jobId);
		if (null != config)
			jobName = config.getJobName();
		return jobName;
	}

	private void processAMSAlerts(final String message,
			final List<List<String>> alertDetails) {
		List<String> alertDetail = new ArrayList<>();
		String[] tokens = StringUtils.delimitedListToStringArray(message, "||");
		String[] eventName = StringUtils.tokenizeToStringArray(tokens[2], " ");
		String summary = getSummary(tokens[6]);
		LOGGER.debug("Found AMS alert with details - eventName[1]: "
				+ eventName[1] + " eventName[0]: " + eventName[0] + " summary: "
				+ summary);
		alertDetail.addAll(Arrays.asList(new String[]{PLATFORM_ALERTS,
				eventName[1], eventName[0], summary}));
		alertDetails.add(alertDetail);
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
			Date alertTimeStamp = new SimpleDateFormat(DATE_FORMATS[0],
					Locale.UK).parse(dateString);
			timeStamp = alertTimeStamp.toString();
			LOGGER.trace("getCTRLMTimeStamp(String) dateString: " + dateString
					+ " parsed timeStamp: " + timeStamp);
		} catch (ParseException exception) {
			LOGGER.error("getCTRLMTimeStamp error occured while parsing date :"
					+ dateString);
		}

		return timeStamp;
	}

	@Override
	protected String getReportName() {
		return "platformalert";
	}

	@Override
	protected void applyTransformations(List<List<String>> details)
			throws ParseException {
	}
}