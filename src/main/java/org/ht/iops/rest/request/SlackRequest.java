package org.ht.iops.rest.request;

import java.util.List;

public class SlackRequest extends RestRequest {
	private String text;
	private boolean notifyChannel;
	private boolean notifyHere;
	private List<Attachment> attachments;

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the notifyChannel
	 */
	public boolean isNotifyChannel() {
		return notifyChannel;
	}

	/**
	 * @param notifyChannel
	 *            the notifyChannel to set
	 */
	public void setNotifyChannel(boolean notifyChannel) {
		this.notifyChannel = notifyChannel;
	}

	/**
	 * @return the notifyHere
	 */
	public boolean isNotifyHere() {
		return notifyHere;
	}

	/**
	 * @param notifyHere
	 *            the notifyHere to set
	 */
	public void setNotifyHere(boolean notifyHere) {
		this.notifyHere = notifyHere;
	}

	@Override
	public String toString() {
		return "SlackRequest [text=" + text + ", notifyChannel=" + notifyChannel
				+ ", notifyHere=" + notifyHere + ", attachments=" + attachments
				+ "]";
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
}
