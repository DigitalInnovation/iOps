package org.ht.iops.events.adapters;

import java.util.ArrayList;
import java.util.List;

import org.ht.iops.db.repository.config.AppConfigRepository;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.rest.request.JiraRestRequest;
import org.ht.iops.rest.response.JiraResponse;
import org.ht.iops.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JiraAdapter
		implements
			RequestAdapter<JiraRestRequest, JiraResponse> {

	@Autowired
	private AppConfigRepository appConfigRepository;

	@Override
	public JiraRestRequest transformRequest(IOpsEvent iOpsEvent) {
		JiraRestRequest request = new JiraRestRequest();
		request.setProjectName(appConfigRepository
				.findByNameAndType("jira", "projectname").getValue());
		request.setSummary(iOpsEvent.getAttributes().get("summary"));
		request.setLabels(iOpsEvent.getAttributes().get("labels"));
		request.setDescription(iOpsEvent.getAttributes().get("description"));
		request.setOwner(iOpsEvent.getAttributes().get("owner"));
		request.setOriginalEstimate(iOpsEvent.getAttributes().get("estimate"));
		return request;
	}

	@Override
	public RestResponse transformResponse(JiraResponse response) {
		List<String> tokens = new ArrayList<>();
		tokens.add(response.getKey());
		tokens.add(response.getSelf());
		response.setTokens(tokens);
		return response;
	}

	@Override
	public Class<JiraResponse> getResponseClass() {
		return JiraResponse.class;
	}
}