package org.ht.iops.rest;

import java.util.HashMap;

import org.ht.iops.events.IOpsEvent;
import org.ht.iops.events.publisher.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlackController {
	@Autowired
	private EventPublisher eventPublisher;

	@RequestMapping(value = "/slack", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String postToSlack() {
		IOpsEvent event = new IOpsEvent("nightly", null);
		HashMap<String, String> attributes = new HashMap<>();
		attributes.put("type", "completed");
		event.setAttributes(attributes);
		eventPublisher.createEvent(event);
		return "Welcome to exciting world of Operation!";
	}
}
