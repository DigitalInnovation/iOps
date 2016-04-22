package org.ht.iops.db.beans;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class SlackConfig {
	@Id
	private ObjectId id;
	private String name;
	private String listner;
	private String type;
	private String text;
	private boolean channel;
	private boolean here;

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
	 * @return the listner
	 */
	public String getListner() {
		return listner;
	}

	/**
	 * @param listner
	 *            the listner to set
	 */
	public void setListner(String listner) {
		this.listner = listner;
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
	 * @return the channel
	 */
	public boolean isChannel() {
		return channel;
	}

	/**
	 * @param channel
	 *            the channel to set
	 */
	public void setChannel(boolean channel) {
		this.channel = channel;
	}

	/**
	 * @return the here
	 */
	public boolean isHere() {
		return here;
	}

	/**
	 * @param here
	 *            the here to set
	 */
	public void setHere(boolean here) {
		this.here = here;
	}
}
