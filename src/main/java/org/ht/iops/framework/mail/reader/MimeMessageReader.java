package org.ht.iops.framework.mail.reader;

import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.ht.iops.framework.mail.MailData;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Mime message reader class. This class is used to parse javax MimeMessage
 * message depending upon the content type of the message.
 * 
 * @author htomar
 *
 */
public class MimeMessageReader {
	/** Logger **/
	private static final Logger logger = LoggerFactory
			.getLogger(MimeMessageReader.class);

	/**
	 * Method to parse mime message.
	 * 
	 * @param message
	 *            - Mime Message to be parsed
	 * @return {@link MailData} mail data
	 * @throws Exception
	 *             if an exception occurs while parsing mime message.
	 */
	public static MailData parseMessage(final MimeMessage message,
			final boolean parseHTMLElements) throws Exception {
		MailData mailData = new MailData();
		MimeMessageParser messageParser = new MimeMessageParser(message);
		messageParser.parse();
		setBasicDetails(mailData, messageParser);

		if (messageParser.hasPlainContent()) {
			mailData.setPlainContent(messageParser.getPlainContent());
		}
		if (messageParser.hasHtmlContent()) {
			mailData.setHtmlContent(messageParser.getHtmlContent());
		}
		if (messageParser.hasAttachments()) {
			mailData.setAttachments(messageParser.getAttachmentList());
		}

		if (parseHTMLElements) {
			parseHTMLContent(mailData);
		}

		logger.debug(
				mailData.getMessageFrom() + " : " + mailData.getPlainContent()
						+ " : " + mailData.getAttachments());
		return mailData;
	}

	/**
	 * This method is used to setting basic details like
	 * <ul>
	 * <li>Parse message subject</li>
	 * <li>Parse message sender, TO, CC & BCC recipients</li>
	 * </ul>
	 * 
	 * @param mailData
	 *            - mail data object
	 * @param message
	 *            - Mime Message to be parsed
	 * @throws Exception
	 *             if an exception occurs while parsing mime message.
	 */
	private static void setBasicDetails(final MailData mailData,
			final MimeMessageParser messageParser) throws Exception {
		mailData.setMessageFrom(messageParser.getFrom());
		mailData.setToAddresses(messageParser.getTo());
		mailData.setCcAddresses(messageParser.getCc());
		mailData.setBccAddresses(messageParser.getBcc());
		mailData.setSubject(messageParser.getSubject());
	}

	/**
	 * This method sets {@link Elements} by parsing HTML content from email.
	 * 
	 * @param mailData
	 *            - mail data object, used for reading HTML content.
	 */
	private static void parseHTMLContent(final MailData mailData) {
		if (!StringUtils.isEmpty(mailData.getHtmlContent())) {
			mailData.setHtmlDocument(Jsoup.parse(mailData.getHtmlContent()));
		}
	}
}
