package org.ht.iops.db.beans.auth;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Credentials {
	@Id
	private ObjectId id;
	private String type;
	private String username;
	private String password;
	private String hostURL;

	public Credentials(String type, String username, String password,
			String hostURL) {
		this.type = type;
		this.username = username;
		this.password = password;
		this.hostURL = hostURL;
	}

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
}
