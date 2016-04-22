package org.ht.iops.service;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.ht.iops.events.IOpsEmailEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Service
public class EmailService {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(EmailService.class);

	@Value("#{credentials.mailUserName}")
	private String username;
	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private VelocityEngine velocityEngine;

	public void sendResponseEmail(IOpsEmailEvent emailEvent) {
		sendEmail(emailEvent.getSender(), emailEvent.getSubject(),
				getHTMLTextFromTemplate(emailEvent.getType(),
						emailEvent.getEventType(), emailEvent.getTokens()),
				true);
	}

	public String getHTMLTextFromTemplate(final String type,
			final String templateName, final Map<String, Object> model) {
		return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
				type + "/" + templateName + ".vm", "UTF-8", model);
	}

	public void sendEmail(final String sender, final String subject,
			final String htmlMessage, final boolean replyMode) {
		LOGGER.debug("Sending email -> To: " + sender + " Subject: " + subject
				+ " Message: " + htmlMessage);
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setSubject(getSubject(subject, replyMode));
			helper.setTo(sender);
			helper.setFrom(new InternetAddress(username + "@sapient.com"));
			helper.setText(htmlMessage, true);
			javaMailSender.send(message);
		} catch (MessagingException messagingException) {
			LOGGER.error(
					"Error sending email : " + messagingException.getMessage());
		}
	}

	private String getSubject(final String subject, final boolean replyMode) {
		String sub = "";
		if (replyMode) {
			sub = "RE: ";
		}
		return sub += subject;
	}
}