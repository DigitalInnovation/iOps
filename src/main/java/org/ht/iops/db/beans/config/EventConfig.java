package org.ht.iops.db.beans.config;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class EventConfig {
	@Id
	private ObjectId id;
	private String name;
	private List<String> events;
	private String adapter;
	private boolean emailRequired;
	private boolean enabled;

	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(ObjectId id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the events
	 */
	public List<String> getEvents() {
		return events;
	}

	/**
	 * @param events
	 *            the events to set
	 */
	public void setEvents(List<String> events) {
		this.events = events;
	}

	/**
	 * @return the adapter
	 */
	public String getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter
	 *            the adapter to set
	 */
	public void setAdapter(String adapter) {
		this.adapter = adapter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EventConfig [name=" + name + ", events=" + events + ", adapter="
				+ adapter + ", emailRequired=" + emailRequired + "]";
	}

	/**
	 * @return the email
	 */
	public boolean isEmailRequired() {
		return emailRequired;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmailRequired(boolean emailRequired) {
		this.emailRequired = emailRequired;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}