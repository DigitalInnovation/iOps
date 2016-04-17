package org.ht.iops.events.adapters;

import org.ht.iops.events.IOpsEvent;
import org.ht.iops.rest.request.SlackRequest;
import org.ht.iops.rest.response.RestResponse;
import org.ht.iops.rest.response.SlackResponse;
import org.springframework.stereotype.Component;

@Component
public class SlackAdapter
		implements
			RequestAdapter<SlackRequest, SlackResponse> {

	@Override
	public SlackRequest transformRequest(IOpsEvent iOpsEvent) {
		return null;
	}

	@Override
	public RestResponse transformResponse(SlackResponse response) {
		return null;
	}

	@Override
	public Class<SlackResponse> getResponseClass() {
		return null;
	}
}
