package org.ht.iops.service;

import org.ht.iops.exception.ApplicationRuntimeException;
import org.ht.iops.exception.ApplicationValidationException;
import org.ht.iops.rest.request.RestRequest;
import org.ht.iops.rest.response.RestErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

@Service
public class IntegrationService {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(IntegrationService.class);

	@Value("${custom.integration.url}")
	private String integrationURL;

	public <Res, Req extends RestRequest> Res processRequest(final String type,
			final String listnerName, final RestRequest request,
			final Class<Res> responseType)
			throws ApplicationValidationException, ApplicationRuntimeException {
		Res response = null;
		LOGGER.debug("Processing request " + type + ", " + listnerName + ", "
				+ request);
		try {
			RestTemplate restTemplate = new RestTemplate();
			response = restTemplate.postForObject(getURL(type, listnerName),
					request, responseType);
		} catch (HttpStatusCodeException errorResponse) {
			handleException(errorResponse, type);
		}
		LOGGER.debug("Response recieved " + response);
		return response;
	}

	private void handleException(final HttpStatusCodeException errorResponse,
			final String type) {
		RestErrorResponse restErrorResponse = (new Gson()).fromJson(
				errorResponse.getResponseBodyAsString(),
				RestErrorResponse.class);
		if (HttpStatus.BAD_REQUEST.equals(errorResponse.getStatusCode())) {
			handle400ErrorResponse(restErrorResponse, type);
		} else {
			handleGenericErrorResponse(restErrorResponse, type);
		}

	}

	private void handle400ErrorResponse(
			final RestErrorResponse restErrorResponse, final String type) {
		throw new ApplicationValidationException(restErrorResponse.getMessage(),
				type);
	}

	private void handleGenericErrorResponse(RestErrorResponse restErrorResponse,
			final String type) {
		throw new ApplicationRuntimeException(restErrorResponse.getMessage(),
				type);
	}

	private String getURL(String type, String listnerName) {
		return integrationURL + "/" + type + "/" + listnerName;
	}
}
