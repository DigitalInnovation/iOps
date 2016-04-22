package org.ht.iops.events.listner;

import java.util.List;

import org.ht.iops.events.AppEvent;
import org.ht.iops.events.EmailEvent;
import org.ht.iops.events.IOpsEmailEvent;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.events.adapters.RequestAdapter;
import org.ht.iops.rest.request.RestRequest;
import org.ht.iops.rest.response.RestResponse;
import org.ht.iops.service.IntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

public class ApplicationEventListner {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(ApplicationEventListner.class);
	private String listnerName;
	private List<String> events;
	private RequestAdapter<RestRequest, RestResponse> requestAdapter;
	private IntegrationService integrationService;
	private boolean emailRequired;

	@EventListener
	public EmailEvent<IOpsEmailEvent> handleCreateEvent(
			AppEvent<IOpsEvent> createEvent) {
		EmailEvent<IOpsEmailEvent> emailEvent = null;
		if (events.contains(createEvent.getSource().getType())) {
			LOGGER.debug(listnerName + " Processing event : "
					+ createEvent.getSource());
			RestRequest request = requestAdapter
					.transformRequest(createEvent.getSource(), listnerName);
			RestResponse response = integrationService.processRequest(
					createEvent.getSource().getType(), listnerName, request,
					requestAdapter.getResponseClass());
			if (emailRequired) {
				LOGGER.debug("Creating email event");
				emailEvent = requestAdapter.createEmailEvent(createEvent,
						response);
			}
		}
		return emailEvent;
	}

	/**
	 * @return the listnerName
	 */
	public String getListnerName() {
		return listnerName;
	}

	/**
	 * @param listnerName
	 *            the listnerName to set
	 */
	public void setListnerName(String listnerName) {
		this.listnerName = listnerName;
	}

	/**
	 * @return the events
	 */
	public List<String> getEvents() {
		return events;
	}

	/**
	 * @param events
	 *            the events to set
	 */
	public void setEvents(List<String> events) {
		this.events = events;
	}

	/**
	 * @return the requestAdapter
	 */
	public RequestAdapter<RestRequest, RestResponse> getRequestAdapter() {
		return requestAdapter;
	}

	/**
	 * @param requestAdapter
	 *            the requestAdapter to set
	 */
	public void setRequestAdapter(
			RequestAdapter<RestRequest, RestResponse> requestAdapter) {
		this.requestAdapter = requestAdapter;
	}

	/**
	 * @return the integrationService
	 */
	public IntegrationService getIntegrationService() {
		return integrationService;
	}

	/**
	 * @param integrationService
	 *            the integrationService to set
	 */
	public void setIntegrationService(IntegrationService integrationService) {
		this.integrationService = integrationService;
	}

	/**
	 * @return the emailRequired
	 */
	public boolean isEmailRequired() {
		return emailRequired;
	}

	/**
	 * @param emailRequired
	 *            the emailRequired to set
	 */
	public void setEmailRequired(boolean emailRequired) {
		this.emailRequired = emailRequired;
	}
}
