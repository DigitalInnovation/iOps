package org.ht.iops.rest.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraRestResponse extends RestResponse {
	private List<Jira> issues;

	private JiraRestResponse() {
	}

	/**
	 * @return the issues
	 */
	public List<Jira> getIssues() {
		return issues;
	}

	/**
	 * @param issues
	 *            the issues to set
	 */
	public void setIssues(List<Jira> issues) {
		this.issues = issues;
	}
}
