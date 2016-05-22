package org.ht.iops.events;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ht.iops.framework.mail.MailData;
import org.ht.iops.rest.request.Attachment;
import org.springframework.util.Assert;

public class IOpsEvent {
	private String type;
	private Map<String, String> attributes;
	private String[] messageArguments;
	private MailData mailData;
	private List<Attachment> attachments;
	private boolean responseRequired;

	protected IOpsEvent(final String type) {
		Assert.hasText(type, "Event type cannot be null");
		this.type = type;
		this.attributes = new HashMap<>();
		this.responseRequired = true;
	}

	public IOpsEvent(final String type, final MailData mailData) {
		this(type);
		this.mailData = mailData;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "IOpsEvent [type=" + type + ", attributes=" + attributes
				+ ", messageArguments=" + Arrays.toString(messageArguments)
				+ ", mailData=" + mailData + ", attachments=" + attachments
				+ ", responseRequired=" + responseRequired + "]";
	}

	/**
	 * @return the mailData
	 */
	public MailData getMailData() {
		return mailData;
	}

	/**
	 * @param mailData
	 *            the mailData to set
	 */
	public void setMailData(MailData mailData) {
		this.mailData = mailData;
	}

	/**
	 * @return the messageArguments
	 */
	public String[] getMessageArguments() {
		return messageArguments;
	}

	/**
	 * @param messageArguments
	 *            the messageArguments to set
	 */
	public void setMessageArguments(String[] messageArguments) {
		this.messageArguments = messageArguments;
	}

	/**
	 * Add attributes to the event.
	 * 
	 * @param attributeName
	 *            - attribute name
	 * @param attributeValue
	 *            - attribute value
	 */
	public void addAttributes(String attributeName, String attributeValue) {
		this.attributes.put(attributeName, attributeValue);
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public boolean isResponseRequired() {
		return responseRequired;
	}

	public void setResponseRequired(boolean responseRequired) {
		this.responseRequired = responseRequired;
	}
}
