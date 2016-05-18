package org.ht.iops.framework.mail.reader.instruction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.ht.iops.db.beans.Status;
import org.ht.iops.db.repository.StatusRepository;
import org.ht.iops.events.IOpsEmailEvent;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.exception.ApplicationException;
import org.ht.iops.exception.ApplicationRuntimeException;
import org.ht.iops.exception.ApplicationValidationException;
import org.ht.iops.framework.mail.MailData;
import org.ht.iops.framework.mail.reader.BaseMailReader;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Component
public abstract class OpsInstructions extends BaseMailReader {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(OpsInstructions.class);

	public OpsInstructions(final StatusRepository statusRepository,
			final EventPublisher eventPublisher) {
		super(statusRepository, eventPublisher);
	}

	@Override
	protected Status postProcess(final MimeMessage message,
			final MailData mailData) throws ApplicationException {
		List<String> subjectTokens = parseSubject(mailData);
		validateSubjectTokens(subjectTokens);
		Map<String, String> bodyTokens = parseBody(mailData);
		validateBodyTokens(bodyTokens);
		try {
			eventPublisher.createEvent(
					createEvent(bodyTokens, subjectTokens, mailData));
		} catch (ApplicationValidationException applicationValidationException) {
			LOGGER.error("Validation error recieved, raising email event");
			eventPublisher.createEvent(new IOpsEmailEvent(
					applicationValidationException, mailData));
		} catch (ApplicationRuntimeException runtimeException) {
			LOGGER.error("Generic error recieved, raising email event");
			eventPublisher.createEvent(
					new IOpsEmailEvent(runtimeException, mailData));
		}
		return null;
	}

	private List<String> parseSubject(final MailData mailData) {
		List<String> subjectTokens = Arrays
				.asList(StringUtils.tokenizeToStringArray(mailData.getSubject(),
						"|", true, false));
		LOGGER.debug("Subject tokens: " + subjectTokens);
		return subjectTokens;
	}

	protected abstract void validateSubjectTokens(
			final List<String> subjectTokens);

	private Map<String, String> parseBody(final MailData mailData) {
		Map<String, String> bodyTokens = new HashMap<>();
		if (!StringUtils.isEmpty(mailData.getPlainContent())) {
			parsePlainBody(mailData.getPlainContent(), bodyTokens);
		} else {
			parseHTMLBody(mailData.getHtmlDocument(), bodyTokens);
		}
		return bodyTokens;
	}

	private void parsePlainBody(final String plainContent,
			final Map<String, String> bodyTokens) {
		List<String> tokens = Arrays.asList(
				StringUtils.tokenizeToStringArray(plainContent, "\r\n"));
		tokens.stream().forEach(token -> {
			String[] keyValuePair = StringUtils.tokenizeToStringArray(token,
					":", true, false);
			bodyTokens.put(keyValuePair[0].toLowerCase(), keyValuePair[1]);
		});
		LOGGER.debug("Plain body tokens: " + bodyTokens);
	}

	protected void parseHTMLBody(final Document htmlDocument,
			final Map<String, String> bodyTokens) {
		final StringBuffer plainContent = new StringBuffer("");
		Elements elements = htmlDocument.getElementsByClass("WordSection1");
		if (!elements.isEmpty()) {
			parseOutlookHTML(plainContent, elements);
		} else {
			parseOutlookRichText(plainContent, htmlDocument);
		}
		LOGGER.debug("Plain text from HTML content: " + plainContent);
		parsePlainBody(plainContent.toString(), bodyTokens);
	}

	protected void parseOutlookRichText(StringBuffer plainContent,
			Document htmlDocument) {
		Elements elements = htmlDocument.getElementsByTag("div");
		elements.stream()
				.filter(divElement -> divElement.toString().contains(":"))
				.forEach(divElement -> divElement.childNodes().forEach(data -> {
					plainContent.append(data.toString())
							.append(System.lineSeparator());
				}));
	}

	protected void parseOutlookHTML(final StringBuffer plainContent,
			Elements elements) {
		elements.first().childNodes().stream()
				.filter(divElement -> divElement.toString().contains("<p"))
				.forEach(pElement -> {
					pElement.childNodes()
							.stream().filter(dataElement -> !dataElement
									.toString().contains("<o:p>"))
							.forEach(data -> {
								if (data.childNodes().isEmpty()) {
									plainContent.append(data.toString());
								} else {
									data.childNodes().stream().forEach(
											dataChild -> plainContent.append(
													dataChild.toString()));
								}
							});
					plainContent.append(System.lineSeparator());
				});
	}

	protected abstract void validateBodyTokens(
			final Map<String, String> bodyTokens);

	@Override
	protected boolean requireHTMLElements() {
		return true;
	}

	protected abstract String getInstructionName();

	protected IOpsEvent createEvent(final Map<String, String> bodyTokens,
			final List<String> subjectTokens, final MailData mailData) {
		Assert.notNull(mailData, "Mail Data cannot be null.");
		validateBodyTokens(bodyTokens);
		Map<String, String> attributes = new HashMap<>();
		attributes.putAll(bodyTokens);
		addSubjectTokens(attributes, subjectTokens);
		IOpsEvent iOpsEvent = createEvent(getInstructionName().toLowerCase(),
				mailData);
		iOpsEvent.setAttributes(attributes);
		return iOpsEvent;
	}

	protected abstract void addSubjectTokens(Map<String, String> instructions,
			List<String> subjectTokens);

	@Override
	protected String getReportName() {
		return null;
	}
}