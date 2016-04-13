package com.mns.ops.framework.mail.reader;

import javax.mail.internet.MimeMessage;

import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mns.ops.db.beans.Status;
import com.mns.ops.db.repository.AppConfigRepository;
import com.mns.ops.db.repository.StatusRepository;
import com.mns.ops.exception.ApplicationException;
import com.mns.ops.framework.mail.MailData;

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
}