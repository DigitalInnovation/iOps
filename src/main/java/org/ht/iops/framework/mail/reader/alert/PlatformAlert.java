package org.ht.iops.framework.mail.reader.alert;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ht.iops.db.repository.StatusRepository;
import org.ht.iops.db.repository.config.AppConfigRepository;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.exception.ApplicationValidationException;
import org.ht.iops.framework.mail.MailData;
import org.ht.iops.framework.mail.reader.BaseMailReader;
import org.jsoup.nodes.Node;

public class PlatformAlert extends AlertReader<Map<String, String>> {

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
	protected List<Map<String, String>> getDetailsFromHTML(MailData mailData) {
		List<Map<String, String>> details = new ArrayList<>();
		try {
			mailData.getHtmlDocument().getElementsByTag("table").first()
					.childNodes().stream()
					.forEach(tBody -> tBody.childNodes().stream().forEach(
							trElement -> parseHTML(trElement, details)));
			details.remove(0);
		} catch (NullPointerException
				| IllegalArgumentException illegalArgumentException) {
			throw new ApplicationValidationException(
					"Invalid HTML document recieved in email.",
					getReportName());
		}
		return details;
	}

	protected void parseHTML(final Node trElement,
			final List<Map<String, String>> details) {
		Map<String, String> data = new HashMap<>();
		data.put("Asset", trElement.childNode(0).toString());
		data.put("Severity", trElement.childNode(1).toString());
		data.put("Mode", trElement.childNode(2).toString());
		data.put("Message", trElement.childNode(3).toString());
		details.add(data);
	}

	@Override
	protected IOpsEvent createAlertEvent(List<Map<String, String>> arguments) {
		return null;
	}

	@Override
	protected void applyTransformations(List<Map<String, String>> details)
			throws ParseException {
	}

	@Override
	protected String getReportName() {
		return "platformalert";
	}

}
