package org.ht.iops.events.adapters;

import static org.springframework.util.StringUtils.hasText;

import java.util.HashMap;
import java.util.Map;

import org.ht.iops.db.beans.config.AppConfig;
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
		setPriority(request, iOpsEvent);
		setCustomFields(request, iOpsEvent);
		setFlags(request, iOpsEvent);
		setJQL(request, iOpsEvent);
		return request;
	}

	private void setJQL(JiraRestRequest request, IOpsEvent iOpsEvent) {
		if (hasText(iOpsEvent.getAttributes().get("jql"))) {
			request.setJql("project=" + request.getProjectName() + "&type="
					+ request.getIssueType() + "&"
					+ iOpsEvent.getAttributes().get("jql"));
		}
	}

	private void setFlags(JiraRestRequest request, IOpsEvent iOpsEvent) {
		if (hasText(iOpsEvent.getAttributes().get("forceCreate"))) {
			request.setForceCreate(Boolean.parseBoolean(
					iOpsEvent.getAttributes().get("forceCreate")));
		}

		if (hasText(iOpsEvent.getAttributes().get("linkJira"))) {
			request.setLinkJira(Boolean
					.parseBoolean(iOpsEvent.getAttributes().get("linkJira")));
		}

		if (hasText(iOpsEvent.getAttributes().get("incident"))) {
			request.setIncident(Boolean
					.parseBoolean(iOpsEvent.getAttributes().get("incident")));
		}
	}

	private void setPriority(JiraRestRequest request, IOpsEvent iOpsEvent) {
		String priority = iOpsEvent.getAttributes().get("priority");
		if (hasText(priority)) {
			AppConfig config = appConfigRepository
					.findByNameAndType("jirapriority", priority);
			if (null != config) {
				request.setPriority(config.getValue());
				if ("Critical".equals(priority) || "High".equals(priority)) {
					String labels = request.getLabels();
					labels = hasText(labels) ? labels + "," : "";
					labels += "Expedite";
					request.setLabels(labels);
				}
			}
		}
	}

	private void setCustomFields(final JiraRestRequest request,
			final IOpsEvent iOpsEvent) {
		Map<String, String> customFields = new HashMap<>();
		customFields.put("customfield_12735",
				iOpsEvent.getAttributes().get("worktype"));
		setPatchFields(request, customFields);
		request.setCustomFields(customFields);
	}

	private void setPatchFields(final JiraRestRequest request,
			final Map<String, String> customFields) {
		customFields.put("customfield_13302", "1970-01-01T00:00:00.0+0000");
		customFields.put("customfield_13301", "1970-01-01T00:00:00.0+0000");
		customFields.put("customfield_13303", "none required");
		customFields.put("customfield_13304", "None");
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
		return new EmailEvent<>(
				new IOpsEmailEvent(createEvent.getSource(), tokens));
	}

	@Override
	public String getAPIType() {
		return "jira";
	}
}