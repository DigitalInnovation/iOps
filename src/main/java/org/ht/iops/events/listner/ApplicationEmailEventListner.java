package org.ht.iops.events.listner;

import org.ht.iops.events.EmailEvent;
import org.ht.iops.events.IOpsEmailEvent;
import org.ht.iops.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEmailEventListner {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(ApplicationEmailEventListner.class);

	@Autowired
	private EmailService emailService;

	@EventListener
	public void handleCreateEvent(EmailEvent<IOpsEmailEvent> emailEvent) {
		LOGGER.debug("Email event recieved " + emailEvent.getSource());
		emailService.sendResponseEmail(emailEvent.getSource());
	}
}
