package org.ht.iops.db.beans.auth;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Credentials {
	@Id
	private ObjectId id;
	private String type;
	private String username;
	private String password;
	private String hostURL;
	private Map<String, String> additional;

	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the hostURL
	 */
	public String getHostURL() {
		return hostURL;
	}

	/**
	 * @return the additional
	 */
	public Map<String, String> getAdditional() {
		return additional;
	}

	/**
	 * @param additional
	 *            the additional to set
	 */
	public void setAdditional(Map<String, String> additional) {
		this.additional = additional;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(ObjectId id) {
		this.id = id;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param hostURL
	 *            the hostURL to set
	 */
	public void setHostURL(String hostURL) {
		this.hostURL = hostURL;
	}
}
