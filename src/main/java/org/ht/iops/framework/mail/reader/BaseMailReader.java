package org.ht.iops.framework.mail.reader;

import static org.ht.iops.framework.mail.reader.MimeMessageReader.parseMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.ht.iops.db.beans.Status;
import org.ht.iops.db.repository.StatusRepository;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.exception.ApplicationException;
import org.ht.iops.framework.mail.MailData;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Abstract mail reader to provide basic functionalities like
 * <ul>
 * <li>Parse message subject</li>
 * <li>Parse message sender, TO, CC & BCC recipients</li>
 * <li>Extract plain content in case of text/plain multipart</li>
 * <li>Extract HTML content and parse it into {@link Elements}</li>
 * <li>Extract attachments</li>
 * </ul>
 * 
 * @author htomar
 *
 */
public abstract class BaseMailReader {
	/** Logger **/
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BaseMailReader.class);

	private StatusRepository statusRepository;
	protected EventPublisher eventPublisher;

	public BaseMailReader(final StatusRepository statusRepository,
			final EventPublisher eventPublisher) {
		this.statusRepository = statusRepository;
		this.eventPublisher = eventPublisher;
	}

	protected void preProcess(final MimeMessage message) {

	}

	public Status readMail(final MimeMessage message)
			throws ApplicationException {
		Status status = null;
		try {
			preProcess(message);
			status = postProcess(message,
					parseMessage(message, requireHTMLElements()));
		} catch (Exception exception) {
			throw new ApplicationException("Generic Exception", exception);
		}
		LOGGER.debug("Finished processing mime message");
		return status;
	}

	protected abstract Status postProcess(final MimeMessage message,
			final MailData mailData) throws ApplicationException;

	public void saveStatus(Status status) {
		if (null != status) {
			statusRepository.save(status);
		}
	}

	protected boolean requireHTMLElements() {
		return false;
	}

	protected IOpsEvent createEvent(final String type) {
		return createEvent(type, null);
	}

	protected IOpsEvent createEvent(final String type,
			final MailData mailData) {
		return new IOpsEvent(type, mailData);
	}

	protected abstract String getReportName();

	protected void publishEvent(final IOpsEvent event) {
		eventPublisher.createEvent(event);
	}

	protected void publishEvent(final List<IOpsEvent> events) {
		if (null != events && !events.isEmpty())
			events.stream().forEach(event -> eventPublisher.createEvent(event));
	}

	protected Map<String, String> parseBody(final MailData mailData) {
		Map<String, String> bodyTokens = null;
		if (!StringUtils.isEmpty(mailData.getPlainContent())) {
			bodyTokens = parsePlainBody(mailData.getPlainContent());
		} else {
			bodyTokens = parseHTMLBody(mailData.getHtmlDocument());
		}
		return bodyTokens;
	}

	protected Map<String, String> parsePlainBody(final String plainContent) {
		Map<String, String> bodyTokens = new HashMap<>();
		List<String> tokens = Arrays.asList(
				StringUtils.tokenizeToStringArray(plainContent, "\r\n"));
		tokens.stream().forEach(token -> {
			String[] keyValuePair = token.split(":", 2);
			if (null != keyValuePair && keyValuePair.length > 1)
				bodyTokens.put(keyValuePair[0].trim().toLowerCase(),
						keyValuePair[1].trim());
		});
		LOGGER.debug("Plain body tokens: " + bodyTokens);
		return bodyTokens;
	}

	protected Map<String, String> parseHTMLBody(final Document htmlDocument) {
		return new HashMap<>();
	}
}