package com.mns.ops.framework.mail.reader.instruction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.mns.ops.db.beans.Status;
import com.mns.ops.exception.ApplicationException;
import com.mns.ops.framework.mail.MailData;
import com.mns.ops.framework.mail.reader.BaseMailReader;

@Component
public abstract class OpsInstructions extends BaseMailReader {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(OpsInstructions.class);

	@Override
	protected Status postProcess(final MimeMessage message,
			final MailData mailData) throws ApplicationException {
		List<String> subjectTokens = parseSubject(mailData);
		LOGGER.debug("Subject tokens: " + subjectTokens);
		validateSubjectTokens(subjectTokens);
		Map<String, String> bodyTokens = parseBody(mailData);
		validateBodyTokens(bodyTokens);
		sendRestRequest(subjectTokens, bodyTokens);
		return null;
	}

	protected List<String> parseSubject(final MailData mailData) {
		return Arrays.asList(StringUtils.tokenizeToStringArray(
				mailData.getSubject(), "|", true, false));
	}

	protected void validateSubjectTokens(final List<String> subjectTokens) {
	}

	protected void validateBodyTokens(final Map<String, String> bodyTokens) {
	}

	protected Map<String, String> parseBody(final MailData mailData) {
		Map<String, String> bodyTokens = new HashMap<>();
		if (!StringUtils.isEmpty(mailData.getPlainContent())) {
			parsePlainBody(mailData.getPlainContent(), bodyTokens);
		} else {
			parseHTMLBody(mailData.getHtmlDocument(), bodyTokens);
		}
		return bodyTokens;
	}

	protected void parsePlainBody(final String plainContent,
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
				.forEach(
						pElement -> pElement.childNodes()
								.stream().filter(dataElement -> !dataElement
										.toString().contains("<o:p>"))
								.forEach(data -> {
									plainContent.append(data.toString())
											.append(System.lineSeparator());
								}));
	}

	@Override
	public Status getStatus(final String... strings) {
		return null;
	}

	@Override
	protected boolean requireHTMLElements() {
		return true;
	}

	protected abstract String getInstructionName();

	protected abstract void sendRestRequest(List<String> subjectTokens,
			Map<String, String> bodyTokens);
}