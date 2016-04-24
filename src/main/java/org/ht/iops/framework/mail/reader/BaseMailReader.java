package org.ht.iops.framework.mail.reader;

import javax.mail.internet.MimeMessage;

import org.ht.iops.db.beans.Status;
import org.ht.iops.db.repository.StatusRepository;
import org.ht.iops.db.repository.config.AppConfigRepository;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.exception.ApplicationException;
import org.ht.iops.framework.mail.MailData;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

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

	@Autowired
	private MimeMessageReader mimeMessageReader;
	@Autowired
	private StatusRepository statusRepository;
	@Autowired
	protected AppConfigRepository appConfigRepository;
	@Autowired
	protected EventPublisher eventPublisher;

	public BaseMailReader(final MimeMessageReader mimeMessageReader,
			final StatusRepository statusRepository,
			final AppConfigRepository appConfigRepository,
			final EventPublisher eventPublisher) {
		this.mimeMessageReader = mimeMessageReader;
		this.statusRepository = statusRepository;
		this.appConfigRepository = appConfigRepository;
		this.eventPublisher = eventPublisher;
	}

	protected void preProcess(final MimeMessage message) {

	}

	public Status readMail(final MimeMessage message)
			throws ApplicationException {
		Status status = null;
		try {
			preProcess(message);
			status = postProcess(message, mimeMessageReader
					.parseMessage(message, requireHTMLElements()));
		} catch (Exception exception) {
			throw new ApplicationException("Generic Exception", exception);
		}
		LOGGER.debug("Finished processing mime message");
		return status;
	}

	protected abstract Status postProcess(final MimeMessage message,
			final MailData mailData) throws ApplicationException;

	public abstract Status getStatus(final String... strings);

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
		Assert.notNull(mailData, "Mail Data cannot be null.");
		return new IOpsEvent(type, mailData);
	}
}