package org.ht.iops.framework.mail.reader.incident;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.ht.iops.db.beans.Status;
import org.ht.iops.db.beans.config.AppConfig;
import org.ht.iops.db.repository.StatusRepository;
import org.ht.iops.db.repository.config.AppConfigRepository;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.exception.ApplicationException;
import org.ht.iops.exception.ApplicationRuntimeException;
import org.ht.iops.exception.ApplicationValidationException;
import org.ht.iops.framework.mail.MailData;
import org.ht.iops.framework.mail.reader.BaseMailReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class IncidentReader extends BaseMailReader {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(IncidentReader.class);

	private final AppConfigRepository configRepository;

	public IncidentReader(final StatusRepository statusRepository,
			final EventPublisher eventPublisher,
			final AppConfigRepository configRepository) {
		super(statusRepository, eventPublisher);
		this.configRepository = configRepository;
	}

	@Override
	protected Status postProcess(MimeMessage message, MailData mailData)
			throws ApplicationException {
		Map<String, String> mailTokens = parseSubject(mailData);
		mailTokens.putAll(parseBody(mailData));
		mailTokens = transformTokens(mailTokens);
		LOGGER.debug("Mail tokens: " + mailTokens);
		try {
			eventPublisher.createEvent(createEvent(mailTokens, mailData));
		} catch (ApplicationValidationException applicationValidationException) {
			LOGGER.error("Validation error recieved.",
					applicationValidationException);
		} catch (ApplicationRuntimeException runtimeException) {
			LOGGER.error("Generic error recieved.", runtimeException);
		}
		return null;
	}

	private Map<String, String> parseSubject(final MailData mailData) {
		hasText(mailData.getSubject(), "Invalid details recieved.");
		String[] tokens = mailData.getSubject().split("-", 3);
		notNull(tokens, "Invalid details recieved.");
		isTrue(tokens.length == 3, "Invalid details recieved.");
		Map<String, String> subjectTokens = new HashMap<>();
		subjectTokens.put("priority", tokens[0].replaceAll("FW: ", "").trim());
		subjectTokens.put("incident", tokens[1].trim());
		subjectTokens.put("details", tokens[2].trim());
		LOGGER.debug("Subject tokens: " + subjectTokens);
		return subjectTokens;
	}

	@Override
	protected Map<String, String> parsePlainBody(final String plainContent) {
		Map<String, String> bodyTokens = super.parsePlainBody(
				plainContent.substring(0, plainContent.indexOf("Notes:")));
		String notes = plainContent.substring(plainContent.indexOf("Notes:"));
		if (null != notes && notes.length() > 2000)
			notes = notes.substring(0, 2000);
		bodyTokens.put("description", notes);
		return bodyTokens;
	}

	private Map<String, String> transformTokens(
			Map<String, String> mailTokens) {
		Map<String, String> transformedTokens = new HashMap<>();
		hasText(mailTokens.get("summary"));
		transformedTokens.put("summary",
				mailTokens.get("incident") + " | " + mailTokens.get("summary"));
		transformedTokens.put("description", mailTokens.get("description"));
		transformedTokens.put("worktype", "Incident");
		getLabels(mailTokens, transformedTokens);
		transformedTokens.put("owner", "ayadav");
		transformedTokens.put("priority", mailTokens.get("priority"));
		transformedTokens.put("linkJira", "true");
		createJQL(mailTokens, transformedTokens);
		return transformedTokens;
	}

	private void createJQL(Map<String, String> mailTokens,
			Map<String, String> transformedTokens) {
		StringBuffer jql = new StringBuffer();
		jql.append("summary~\"").append(mailTokens.get("incident"))
				.append("\"&").append("\"Work Type\"=\"Incident\"")
				.append("order by created desc");
		transformedTokens.put("jql", jql.toString());
	}

	private void getLabels(final Map<String, String> mailTokens,
			final Map<String, String> transformedTokens) {
		String queueLabel = resolveQueue(
				mailTokens.get("details").split(":")[0].trim());
		if (StringUtils.hasText(queueLabel)) {
			transformedTokens.put("labels", queueLabel);
		}
	}

	private String resolveQueue(final String queueName) {
		AppConfig config = configRepository.findByNameAndType("queuename",
				queueName);
		notNull(config, "Invalid details recieved.");
		return config.getValue();
	}

	protected IOpsEvent createEvent(final Map<String, String> mailTokens,
			final MailData mailData) {
		IOpsEvent iOpsEvent = createEvent("jira", mailData);
		iOpsEvent.setAttributes(mailTokens);
		iOpsEvent.setResponseRequired(false);
		return iOpsEvent;
	}

	@Override
	protected String getReportName() {
		return "incident";
	}
}
