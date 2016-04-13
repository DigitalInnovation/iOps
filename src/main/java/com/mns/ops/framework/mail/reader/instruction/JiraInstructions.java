package com.mns.ops.framework.mail.reader.instruction;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.mns.ops.exception.ApplicationValidationException;
import com.mns.ops.rest.request.JiraRestRequest;

@Component
public class JiraInstructions extends OpsInstructions {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(JiraInstructions.class);
	@Value("${custom.integration.url}")
	private String integrationURL;

	@Override
	protected String getInstructionName() {
		return "Jira";
	}

	@Override
	protected void validateSubjectTokens(List<String> subjectTokens) {
		super.validateSubjectTokens(subjectTokens);
		if (subjectTokens.size() < 3
				|| StringUtils.isEmpty(subjectTokens.get(2))) {
			throw new ApplicationValidationException(
					"Jira summary is a required field.", getInstructionName());
		}
	}

	@Override
	protected void validateBodyTokens(Map<String, String> bodyTokens) {
		super.validateBodyTokens(bodyTokens);
		String description = bodyTokens.get("description");
		String owner = bodyTokens.get("owner");
		if (StringUtils.isEmpty(description) || StringUtils.isEmpty(owner)) {
			throw new ApplicationValidationException(
					"Jira description and owner is a required field.",
					getInstructionName());
		}
	}

	@Override
	protected void sendRestRequest(List<String> subjectTokens,
			Map<String, String> bodyTokens) {
		JiraRestRequest request = createRestRequest(subjectTokens, bodyTokens);
		/*
		 * RestTemplate restTemplate = new RestTemplate(); JiraResponse
		 * jiraResponse = restTemplate.postForObject( integrationURL +
		 * "/jira/mnsjira", request, JiraResponse.class);
		 * LOGGER.debug(jiraResponse.toString());
		 */
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