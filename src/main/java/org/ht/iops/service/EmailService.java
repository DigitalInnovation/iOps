package org.ht.iops.service;

import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.ht.iops.events.IOpsEmailEvent;
import org.ht.iops.exception.ApplicationRuntimeException;
import org.ht.iops.exception.ApplicationValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;

/**
 * Class for managing JavaMail tasks (like sending email). This class uses
 * {@link JavaMailSender} for entire JavaMail management. This class supports
 * Thymeleaf templates for sending HTML messages and templates are managed by
 * {@link TemplateEngine}.
 * 
 * <p>
 * Clients will typically receive the <tt>EmailService</tt> reference through
 * dependency injection. {@link JavaMailSender} and {@link TemplateEngine} will
 * be injected automatically.
 * </p>
 * 
 * @author Himanshu Tomar
 *
 */
@Service
public class EmailService {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(EmailService.class);

	@Value("#{credentials.mailUserName}")
	private String username;

	private JavaMailSender javaMailSender;
	private TemplateEngine templateEngine;

	/**
	 * <p>
	 * Constructor for <tt>EmailService</tt> objects.
	 * </p>
	 * <p>
	 * This is the only way to create a <tt>EmailService</tt> instance. Spring
	 * will inject {@link JavaMailSender} and {@link TemplateEngine} using
	 * constructor wiring.
	 * </p>
	 * 
	 * @param javaMailSender
	 *            - for managing JavaMail.
	 * @param templateEngine
	 *            - for managing Thymeleaf templates.
	 */
	public EmailService(final JavaMailSender javaMailSender,
			final TemplateEngine templateEngine) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
	}

	/**
	 * This method is used for sending email based on an event that has
	 * occurred. This method accepts {@link IOpsEmailEvent} event and extracts
	 * information from the event as follows:
	 * <ul>
	 * <li>Type - this tells the type of email event. e.g. "jira"</li>
	 * <li>EventType - this tells the type of email to be sent. e.g. "success",
	 * "validation failure"</li>
	 * <li>Tokens - {@link Map} containing all the data and is used as model for
	 * Thymeleaf.</li>
	 * </ul>
	 * 
	 * <p>
	 * This method will throw {@link ApplicationValidationException} for any
	 * input validation failures (like empty or null Type).
	 * {@link ApplicationRuntimeException} will be thrown if the template could
	 * not be found.
	 * </p>
	 * 
	 * @param emailEvent
	 *            - the event object.
	 */
	public void sendEmailForEvent(IOpsEmailEvent emailEvent) {
		try {
			Assert.notNull(emailEvent, "Email event cannot be null.");
			Assert.hasText(emailEvent.getType(), "Event Type cannot be null.");
			Assert.hasText(emailEvent.getEventType(),
					"Email Type for event cannot be null.");
			Assert.notEmpty(emailEvent.getTokens(), "Tokens cannot be null.");
			String htmlMessage = getHTMLTextFromTemplate(emailEvent.getType(),
					emailEvent.getEventType(), emailEvent.getTokens());
			sendEmail(emailEvent.getSender(), emailEvent.getSubject(),
					htmlMessage, true);
		} catch (IllegalArgumentException illegalArgumentException) {
			throw new ApplicationValidationException(
					illegalArgumentException.getMessage(), null);
		} catch (TemplateInputException inputException) {
			LOGGER.error(inputException.getMessage());
			throw new ApplicationRuntimeException(inputException.getMessage(),
					emailEvent.getType());
		}
	}

	private String getHTMLTextFromTemplate(final String type,
			final String templateName, final Map<String, Object> model) {
		Context context = new Context(Locale.UK, model);
		return templateEngine.process(type + "/" + templateName, context);
	}

	/**
	 * This method is used for sending email based on input parameters.
	 * <p>
	 * This method will throw {@link ApplicationValidationException} for any
	 * input validation failures (like empty or null subject).
	 * {@link ApplicationRuntimeException} will be thrown if email sending
	 * fails.
	 * </p>
	 * 
	 * @param emailTo
	 *            - the recipient of the email.
	 * @param subject
	 *            - email subject.
	 * @param htmlMessage
	 *            - the html text that will be sent as the message body.
	 * @param replyMode
	 *            - this tells whether to append "RE: " (as done by Microsoft
	 *            Outlook) to the email subject.
	 */
	public void sendEmail(final String emailTo, final String subject,
			final String htmlMessage, final boolean replyMode) {
		LOGGER.debug(
				"Sending email -> To: " + emailTo + " Subject: " + subject);
		try {
			Assert.hasText(emailTo, "Email TO cannot be null.");
			Assert.hasText(subject, "Email subject cannot be null.");
			Assert.hasText(htmlMessage, "Email message cannnot be null.");
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setSubject(createSubjectForReplyMode(subject, replyMode));
			helper.setTo(emailTo);
			helper.setFrom(new InternetAddress(username + "@sapient.com"));
			helper.setText(htmlMessage, true);
			javaMailSender.send(message);
		} catch (IllegalArgumentException illegalArgumentException) {
			throw new ApplicationValidationException(
					illegalArgumentException.getMessage(), null);
		} catch (MessagingException | MailSendException messagingException) {
			LOGGER.error(
					"Error sending email : " + messagingException.getMessage());
			throw new ApplicationRuntimeException(null, messagingException);
		}
	}

	private String createSubjectForReplyMode(final String subject,
			final boolean replyMode) {
		String suffix = "";
		if (replyMode) {
			suffix = "RE: ";
		}
		return suffix + subject;
	}
}