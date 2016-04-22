package org.ht.iops.events.adapters;

import org.ht.iops.db.beans.SlackConfig;
import org.ht.iops.db.repository.SlackConfigRepository;
import org.ht.iops.events.AppEvent;
import org.ht.iops.events.EmailEvent;
import org.ht.iops.events.IOpsEmailEvent;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.rest.request.SlackRequest;
import org.ht.iops.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SlackAdapter
		implements
			RequestAdapter<SlackRequest, RestResponse> {
	@Autowired
	private SlackConfigRepository slackConfigRepository;

	@Override
	public SlackRequest transformRequest(final IOpsEvent iOpsEvent,
			final String... additionalAttributes) {
		SlackConfig config = slackConfigRepository.findByNameAndListnerAndType(
				iOpsEvent.getType(), additionalAttributes[0],
				iOpsEvent.getAttributes().get("type"));
		SlackRequest request = new SlackRequest();
		request.setText(config.getText());
		request.setNotifyChannel(config.isChannel());
		request.setNotifyHere(config.isHere());
		return request;
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

	@Override
	public String getAPIType() {
		return "slack";
	}
}
