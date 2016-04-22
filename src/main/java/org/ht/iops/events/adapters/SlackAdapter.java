package org.ht.iops.events.adapters;

import org.ht.iops.events.AppEvent;
import org.ht.iops.events.EmailEvent;
import org.ht.iops.events.IOpsEmailEvent;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.rest.request.SlackRequest;
import org.ht.iops.rest.response.RestResponse;
import org.springframework.stereotype.Component;

@Component
public class SlackAdapter
		implements
			RequestAdapter<SlackRequest, RestResponse> {

	@Override
	public SlackRequest transformRequest(final IOpsEvent iOpsEvent,
			final String... additionalAttributes) {
		return null;
	}

	@Override
	public Class<RestResponse> getResponseClass() {
		return RestResponse.class;
	}

	@Override
	public EmailEvent<IOpsEmailEvent> createEmailEvent(
			final AppEvent<IOpsEvent> createEvent, RestResponse response) {
		return null;
	}
}
