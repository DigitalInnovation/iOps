package org.ht.iops.events.publisher;

import org.ht.iops.events.AppEvent;
import org.ht.iops.events.EmailEvent;
import org.ht.iops.events.IOpsEmailEvent;
import org.ht.iops.events.IOpsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(EventPublisher.class);
	private final ApplicationEventPublisher eventPublisher;

	@Autowired
	public EventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void createEvent(IOpsEvent event) {
		LOGGER.debug("Publishing application event : " + event);
		this.eventPublisher.publishEvent(new AppEvent<IOpsEvent>(event));
	}

	public void createEvent(IOpsEmailEvent event) {
		LOGGER.debug("Publishing email event : " + event);
		this.eventPublisher.publishEvent(new EmailEvent<IOpsEmailEvent>(event));
	}
}
