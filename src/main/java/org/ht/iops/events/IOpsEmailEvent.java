package org.ht.iops.events;

import java.util.List;

public class IOpsEmailEvent {
	private String type;
	private String subject;
	private String sender;
	private List<String> tokens;

	public IOpsEmailEvent(final String type, final String subject,
			final String sender, final List<String> tokens) {
		this.type = type;
		this.subject = subject;
		this.sender = sender;
		this.tokens = tokens;
	}

	public IOpsEmailEvent(final IOpsEvent event, final List<String> tokens) {
		this(event.getType(), event.getMailData().getSubject(),
				event.getMailData().getMessageFrom(), tokens);
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @return the tokens
	 */
	public List<String> getTokens() {
		return tokens;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IOpsEmailEvent [type=" + type + ", subject=" + subject
				+ ", sender=" + sender + ", tokens=" + tokens + "]";
	}
}