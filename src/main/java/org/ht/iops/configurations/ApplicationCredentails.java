package org.ht.iops.configurations;

import java.util.List;

import javax.annotation.PostConstruct;

import org.ht.iops.db.beans.auth.Credentials;
import org.ht.iops.db.repository.auth.CredentialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration("credentials")
public class ApplicationCredentails {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ApplicationCredentails.class);

	@Autowired
	private CredentialRepository credentialRepository;

	List<Credentials> credentials = null;
	private String mailCredentials = "";
	private String mailHost = "";
	private String mailUserName = "";
	private String mailPassword = "";
	private String folder = "";

	@PostConstruct
	public void initialize() {
		credentials = credentialRepository.findAll();
		credentials.stream().filter(p -> "Mail".equals(p.getType()))
				.forEach((p -> setMailCredentials(p)));
		LOGGER.debug("mail: " + this.mailUserName);
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

	private String getCredentials(Credentials credentials,
			boolean mailCredentials) {
		String password = credentials.getPassword();
		if (mailCredentials) {
			this.mailUserName = credentials.getUsername();
			this.mailPassword = password;
			password = password.replaceAll("@", "%40");
			this.folder = credentials.getAdditional().get("folder");
		}
		return credentials.getUsername() + ":" + password;
	}

	/**
	 * @return the mailHost
	 */
	public String getMailHost() {
		return mailHost;
	}

	/**
	 * @return the mailUserName
	 */
	public String getMailUserName() {
		return mailUserName;
	}

	/**
	 * @return the mailPassword
	 */
	public String getMailPassword() {
		return mailPassword;
	}

	/**
	 * @return the folder
	 */
	public String getFolder() {
		return folder;
	}
}
