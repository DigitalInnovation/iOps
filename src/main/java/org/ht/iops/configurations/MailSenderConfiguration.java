package org.ht.iops.configurations;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailSenderConfiguration {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(MailSenderConfiguration.class);

	@Value("#{credentials.mailHost}")
	private String host;
	@Value("#{credentials.mailUserName}")
	private String username;
	@Value("#{credentials.mailPassword}")
	private String password;
	@Value("${custom.mail.protocol}")
	private String protocol;
	@Value("${custom.mail.port}")
	private String port;
	@Value("${custom.mail.auth}")
	private String auth;
	@Value("${custom.mail.debug}")
	private String debug;
	@Value("${custom.mail.socketFactory}")
	private String socketFactory;
	@Value("${custom.mail.fallback}")
	private String fallback;

	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(host);
		javaMailSender.setUsername(username);
		javaMailSender.setPassword(password);
		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.transport.protocol", protocol);
		javaMailProperties.put("mail.smtp.port", port);
		javaMailProperties.put("mail.smtp.auth", auth);
		javaMailProperties.put("mail.debug", debug);
		javaMailProperties.put("mail.smtp.socketFactory.class", socketFactory);
		javaMailProperties.put("mail.smtp.socketFactory.fallback", fallback);
		javaMailProperties.put("mail.smtp.ssl.trust", host);
		javaMailSender.setJavaMailProperties(javaMailProperties);
		LOGGER.debug("Mail details : " + host + " protocol " + protocol
				+ " username " + username);
		LOGGER.debug("Mail properties : " + javaMailProperties);
		return javaMailSender;
	}
}
