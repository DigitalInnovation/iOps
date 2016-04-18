package org.ht.iops.events;

import java.util.ArrayList;
import java.util.List;

import org.ht.iops.exception.ApplicationRuntimeException;
import org.ht.iops.exception.ApplicationValidationException;
import org.ht.iops.framework.mail.MailData;

public class IOpsEmailEvent {
	private String type;
	private String subject;
	private String sender;
	private List<String> tokens;
	private EventType eventType;

	public static enum EventType {
		SUCCESS("success"), VALIDATIONERROR("validationerror"), GENERICERROR(
				"genericerror");

		private EventType(final String _type) {
			this._type = _type;
		}

		private final String _type;
	}

	public IOpsEmailEvent(final String type, final String subject,
			final String sender, final List<String> tokens,
			final EventType eventType) {
		this.type = type;
		this.subject = subject;
		this.sender = sender;
		this.tokens = tokens;
		this.eventType = eventType;
	}

	public IOpsEmailEvent(final String type, final String subject,
			final String sender, final List<String> tokens) {
		this(type, subject, sender, tokens, EventType.SUCCESS);
	}

	public IOpsEmailEvent(final String type, final MailData mailData,
			final List<String> tokens, final EventType eventType) {
		this(type, mailData.getSubject(), mailData.getMessageFrom(), tokens,
				eventType);
	}

	public IOpsEmailEvent(final String type, final MailData mailData,
			final List<String> tokens) {
		this(type, mailData.getSubject(), mailData.getMessageFrom(), tokens);
	}

	public IOpsEmailEvent(final IOpsEvent event, final List<String> tokens) {
		this(event.getType(), event.getMailData(), tokens);
	}

	public IOpsEmailEvent(
			final ApplicationValidationException validationException,
			final MailData mailData) {
		this(validationException.getType(), mailData, null,
				EventType.VALIDATIONERROR);
		this.tokens = new ArrayList<>();
		tokens.add(validationException.getMessage());
		tokens.add("");
	}

	public IOpsEmailEvent(final ApplicationRuntimeException runtimeException,
			final MailData mailData) {
		this(runtimeException.getType(), mailData, null,
				EventType.GENERICERROR);
		this.tokens = new ArrayList<>();
		tokens.add(runtimeException.getMessage());
		tokens.add("");
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

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType._type;
	}
}