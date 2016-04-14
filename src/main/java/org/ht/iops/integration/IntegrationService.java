package org.ht.iops.integration;

import java.util.List;
import java.util.Map;

import org.ht.iops.rest.request.JiraRestRequest;
import org.ht.iops.rest.response.JiraResponse;
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

	public JiraResponse processRequest(List<String> subjectTokens,
			Map<String, String> bodyTokens) {
		JiraRestRequest request = createRestRequest(subjectTokens, bodyTokens);
		RestTemplate restTemplate = new RestTemplate();
		JiraResponse jiraResponse = restTemplate.postForObject(
				integrationURL + "/jira/mnsjira", request, JiraResponse.class);
		LOGGER.debug(jiraResponse.toString());
		return jiraResponse;
	}

	private JiraRestRequest createRestRequest(List<String> subjectTokens,
			Map<String, String> bodyTokens) {
		JiraRestRequest jiraRestRequest = new JiraRestRequest();
		jiraRestRequest.setProjectName("PISP");
		jiraRestRequest.setSummary(subjectTokens.get(2));
		if (subjectTokens.size() > 2) {
			jiraRestRequest.setLabels(subjectTokens.get(3));
		}
		jiraRestRequest.setDescription(bodyTokens.get("description"));
		jiraRestRequest.setOwner(bodyTokens.get("owner"));
		jiraRestRequest.setOriginalEstimate(bodyTokens.get("estimate"));
		return jiraRestRequest;
	}
}
