package com.mns.ops.framework.mail.reciever;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mns.ops.exception.ApplicationException;
import com.mns.ops.framework.mail.reader.BaseMailReader;
import com.mns.ops.framework.mail.reader.MailReaderFactory;

@Service
public class MailReceiverService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MailReceiverService.class);

	@Autowired
	private MailReaderFactory factory;

	public void receive(MimeMessage mimeMessage) {
		try {
			BaseMailReader mailReader = factory.getReader(mimeMessage);
			if (null != mailReader) {
				mailReader.saveStatus(mailReader.readMail(mimeMessage));
			}
		} catch (ApplicationException exception) {
			LOGGER.error("An exception occured while parsing message",
					exception);
		}
	}
}
