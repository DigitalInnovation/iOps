package org.ht.iops.rest.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraRestRequest extends RestRequest {
	private String projectName;
	private String summary;
	private String description;
	private String labels;
	private String owner;
	private String originalEstimate;
	private String remainingEstimate;
	private String issueType;
	private Map<String, String> customFields;
	private String priority;
	private boolean forceCreate;
	private String jql;
	private boolean linkJira;
	private boolean incident;

	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary
	 *            the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the labels
	 */
	public String getLabels() {
		return labels;
	}

	/**
	 * @param labels
	 *            the labels to set
	 */
	public void setLabels(String labels) {
		this.labels = labels;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName
	 *            the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the originalEstimate
	 */
	public String getOriginalEstimate() {
		return originalEstimate;
	}

	/**
	 * @param originalEstimate
	 *            the originalEstimate to set
	 */
	public void setOriginalEstimate(String originalEstimate) {
		this.originalEstimate = originalEstimate;
	}

	/**
	 * @return the remainingEstimate
	 */
	public String getRemainingEstimate() {
		return remainingEstimate;
	}

	/**
	 * @param remainingEstimate
	 *            the remainingEstimate to set
	 */
	public void setRemainingEstimate(String remainingEstimate) {
		this.remainingEstimate = remainingEstimate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JiraRestRequest [projectName=" + projectName + ", summary="
				+ summary + ", description=" + description + ", labels="
				+ labels + ", owner=" + owner + ", originalEstimate="
				+ originalEstimate + ", remainingEstimate=" + remainingEstimate
				+ ", issueType=" + issueType + ", customFields=" + customFields
				+ ", priority=" + priority + ", forceCreate=" + forceCreate
				+ ", jql=" + jql + ", linkJira=" + linkJira + ", incident="
				+ incident + "]";
	}

	/**
	 * @return the issueType
	 */
	public String getIssueType() {
		return issueType;
	}

	/**
	 * @param issueType
	 *            the issueType to set
	 */
	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public Map<String, String> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, String> customFields) {
		this.customFields = customFields;
	}

	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	 * @return the forceCreate
	 */
	public boolean isForceCreate() {
		return forceCreate;
	}

	/**
	 * @param forceCreate
	 *            the forceCreate to set
	 */
	public void setForceCreate(boolean forceCreate) {
		this.forceCreate = forceCreate;
	}

	/**
	 * @return the jql
	 */
	public String getJql() {
		return jql;
	}

	/**
	 * @param jql
	 *            the jql to set
	 */
	public void setJql(String jql) {
		this.jql = jql;
	}

	/**
	 * @return the linkJira
	 */
	public boolean isLinkJira() {
		return linkJira;
	}

	/**
	 * @param linkJira
	 *            the linkJira to set
	 */
	public void setLinkJira(boolean linkJira) {
		this.linkJira = linkJira;
	}

	/**
	 * @return the incident
	 */
	public boolean isIncident() {
		return incident;
	}

	/**
	 * @param incident
	 *            the incident to set
	 */
	public void setIncident(boolean incident) {
		this.incident = incident;
	}
}
