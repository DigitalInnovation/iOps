package org.ht.iops.framework.mail.reader;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.mail.internet.MimeMessage;

import org.ht.iops.db.beans.Status;
import org.ht.iops.exception.ApplicationException;
import org.ht.iops.framework.mail.MailData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncCallReader extends BaseMailReader {
	final private static String SUBJECT_PREFIX = "Ecom Splunk Alert | Major | Hourly | ";
	final private static Logger LOGGER = LoggerFactory
			.getLogger(SyncCallReader.class);

	@Override
	protected Status postProcess(final MimeMessage message,
			final MailData mailData) throws ApplicationException {
		Status status = null;
		final String reportName = mailData.getSubject()
				.replaceAll(SUBJECT_PREFIX, "");
		if (null == mailData.getHtmlDocument()) {
			throw new ApplicationException(reportName,
					"Invalid message body contents, no HTML document");
		}

		List<String> details = new ArrayList<>();
		try {
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
			status = getStatus(reportName, getDescription(details));
		} catch (ParseException exception) {
			LOGGER.debug("HTML Content: " + mailData.getHtmlContent());
			throw new ApplicationException(
					"An error occured while parsing HTML body", reportName,
					exception);
		}
		return status;
	}

	private String getDescription(final List<String> details)
			throws ParseException {
		return MessageFormat.format(
				appConfigRepository.findByNameAndType("SyncCall", "Message")
						.getValue(),
				getReportTime(details.get(0)), details.get(1), details.get(2),
				details.get(3));
	}

	private String getReportTime(final String reportTime)
			throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(appConfigRepository
				.findByNameAndType("SyncCall", "DateFormat").getValue(),
				Locale.ENGLISH);
		return dateFormat.parse(reportTime).toString();
	}

	@Override
	public Status getStatus(final String... strings) {
		Status status = new Status(strings[0], "Red");
		status.setLastUpdate(new Date());
		status.setUrl(strings[0]);
		status.setType("keyValue");
		status.setSection("sync call");
		status.setReason(strings[1]);
		status.setOverview(strings[1]);
		LOGGER.debug("status : " + status);
		return status;
	}

	@Override
	protected boolean requireHTMLElements() {
		return true;
	}
}