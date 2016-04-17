package org.ht.iops.service;

import org.ht.iops.rest.request.RestRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IntegrationService {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(IntegrationService.class);

	@Value("${custom.integration.url}")
	private String integrationURL;

	public <Res, Req extends RestRequest> Res processRequest(final String type,
			final String listnerName, final RestRequest request,
			final Class<Res> responseType) {
		LOGGER.debug("Processing request " + type + ", " + listnerName + ", "
				+ request);
		RestTemplate restTemplate = new RestTemplate();
		Res response = restTemplate.postForObject(getURL(type, listnerName),
				request, responseType);
		LOGGER.debug("Response recieved " + response);
		return response;
	}

	private String getURL(String type, String listnerName) {
		return integrationURL + "/" + type + "/" + listnerName;
	}
}
