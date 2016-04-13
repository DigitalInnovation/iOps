package com.mns.ops.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpsRestController {
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String index() {
		return "Welcome to exciting world of Operation!";
	}

	@RequestMapping(value = "/404", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String error404() {
		return "Oops, looks like you are lost. The page could not be found.";
	}

	@RequestMapping(value = "/500", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String error500() {
		return "Oops, you broke us. An internal server error has occured.";
	}
}
