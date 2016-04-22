package org.ht.iops.events.adapters;

import java.util.HashMap;
import java.util.Map;

import org.ht.iops.db.repository.config.AppConfigRepository;
import org.ht.iops.events.AppEvent;
import org.ht.iops.events.EmailEvent;
import org.ht.iops.events.IOpsEmailEvent;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.rest.request.JiraRestRequest;
import org.ht.iops.rest.response.JiraRestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JiraAdapter
		implements
			RequestAdapter<JiraRestRequest, JiraRestResponse> {

	@Autowired
	private AppConfigRepository appConfigRepository;

	@Override
	public JiraRestRequest transformRequest(final IOpsEvent iOpsEvent,
			final String... additionalAttributes) {
		JiraRestRequest request = new JiraRestRequest();
		request.setProjectName(appConfigRepository
				.findByNameAndType(additionalAttributes[0], "projectname")
				.getValue());
		request.setSummary(iOpsEvent.getAttributes().get("summary"));
		request.setLabels(iOpsEvent.getAttributes().get("labels"));
		request.setDescription(iOpsEvent.getAttributes().get("description"));
		request.setOwner(iOpsEvent.getAttributes().get("owner"));
		request.setOriginalEstimate(iOpsEvent.getAttributes().get("estimate"));
		request.setIssueType(appConfigRepository
				.findByNameAndType(additionalAttributes[0], "issuetype")
				.getValue());
		return request;
	}

	@Override
	public Class<JiraRestResponse> getResponseClass() {
		return JiraRestResponse.class;
	}

	@Override
	public EmailEvent<IOpsEmailEvent> createEmailEvent(
			final AppEvent<IOpsEvent> createEvent, JiraRestResponse response) {
		Map<String, Object> tokens = new HashMap<>();
		tokens.put("summary",
				createEvent.getSource().getMailData().getSubject());
		tokens.put("issues", response.getIssues());
		return new EmailEvent<IOpsEmailEvent>(
				new IOpsEmailEvent(createEvent.getSource(), tokens));
	}
}