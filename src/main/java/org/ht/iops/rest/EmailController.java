package org.ht.iops.rest;

import org.ht.iops.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
	@Autowired
	private EmailService emailService;

	@RequestMapping(value = "/email", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String email() {
		emailService.sendEmail("htomar@sapient.com", "Test emails",
				"this is a html test mail", false);
		return "Welcome to exciting world of Operation!";
	}
}
