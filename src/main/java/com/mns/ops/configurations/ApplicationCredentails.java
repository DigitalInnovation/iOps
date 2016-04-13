package com.mns.ops.configurations;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.mns.ops.db.beans.Credentials;
import com.mns.ops.db.repository.CredentialRepository;

@Configuration("credentials")
public class ApplicationCredentails {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ApplicationCredentails.class);

	@Autowired
	private CredentialRepository credentialRepository;

	List<Credentials> credentials = null;
	private String mailCredentials = "";
	private String mailHost = "";
	private String akamaiCredentials = "";
	private String akamaiHost = "";

	@PostConstruct
	public void initialize() {
		credentials = credentialRepository.findAll();
		credentials.stream().filter(p -> "Mail".equals(p.getType()))
				.forEach((p -> setMailCredentials(p)));
		credentials.stream().filter(p -> "Akamai".equals(p.getType()))
				.forEach((p -> setAkamaiCredentials(p)));
		LOGGER.debug("mail: " + this.mailCredentials + "; Akamai: "
				+ this.akamaiCredentials);
	}

	/**
	 * @return the mailCredentials
	 */
	public String getMailCredentials() {
		return mailCredentials;
	}

	private void setMailCredentials(Credentials credentials) {
		this.mailCredentials = getCredentials(credentials, true);
		this.mailHost = credentials.getHostURL();
	}

	private void setAkamaiCredentials(Credentials credentials) {
		this.akamaiCredentials = getCredentials(credentials, false);
		this.akamaiHost = credentials.getHostURL();
	}

	private String getCredentials(Credentials credentials,
			boolean mailCredentials) {
		String password = credentials.getPassword();
		if (mailCredentials) {
			password = password.replaceAll("@", "%40");
		}
		return credentials.getUsername() + ":" + password;
	}

	/**
	 * @return the akamaiCredentials
	 */
	public String getAkamaiCredentials() {
		return akamaiCredentials;
	}

	/**
	 * @return the mailHost
	 */
	public String getMailHost() {
		return mailHost;
	}

	/**
	 * @return the akamaiHost
	 */
	public String getAkamaiHost() {
		return akamaiHost;
	}
}
