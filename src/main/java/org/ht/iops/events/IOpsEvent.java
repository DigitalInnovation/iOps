package org.ht.iops.events;

import java.util.HashMap;
import java.util.Map;

import org.ht.iops.framework.mail.MailData;
import org.springframework.util.Assert;

public class IOpsEvent {
	private String type;
	private Map<String, String> attributes;
	private String[] messageArguments;
	private MailData mailData;

	protected IOpsEvent(final String type) {
		Assert.hasText(type, "Event type cannot be null");
		this.type = type;
		this.attributes = new HashMap<>();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Event [type=" + type + ", attributes=" + attributes + "]";
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
}
