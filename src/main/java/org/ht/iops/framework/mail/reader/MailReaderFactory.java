package org.ht.iops.framework.mail.reader;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.ht.iops.framework.mail.reader.alert.SyncCallAlert;
import org.ht.iops.framework.mail.reader.instruction.InvalidInstructions;
import org.ht.iops.framework.mail.reader.instruction.JiraInstructions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MailReaderFactory {
	private final Logger LOGGER = LoggerFactory
			.getLogger(MailReaderFactory.class);

	private CPUStatsReader cpuStatsReader;
	private ThreadStatsReader threadStatsReader;
	private JiraInstructions jiraInstructions;
	private InvalidInstructions invalidInstructions;
	private SyncCallAlert syncCallReader;

	public MailReaderFactory(final CPUStatsReader cpuStatsReader,
			final ThreadStatsReader threadStatsReader,
			final JiraInstructions jiraInstructions,
			final InvalidInstructions invalidInstructions,
			final SyncCallAlert syncCallReader) {
		this.cpuStatsReader = cpuStatsReader;
		this.threadStatsReader = threadStatsReader;
		this.jiraInstructions = jiraInstructions;
		this.invalidInstructions = invalidInstructions;
		this.syncCallReader = syncCallReader;
	}

	public BaseMailReader getReader(MimeMessage message) {
		BaseMailReader mailReader = null;
		try {
			if (message.getSubject()
					.contains("PLATFORM_SUPPORT_APP_CPU_USAGE")) {
				mailReader = cpuStatsReader;
			} else if (message.getSubject()
					.contains("PLATFORM_SUPPORT_APP_THREAD_COUNT")) {
				mailReader = threadStatsReader;
			} else if (StringUtils.startsWithIgnoreCase(message.getSubject(),
					"iOps")) {
				mailReader = createInstructionsReader(message.getSubject());
			} else if (StringUtils.startsWithIgnoreCase(message.getSubject(),
					"Ecom Splunk Alert | Major")) {
				mailReader = createSplunkReportReader(message.getSubject());
			}
		} catch (MessagingException exception) {
			LOGGER.error("Error occured while parsing message", exception);
		}
		if (LOGGER.isDebugEnabled() && null != mailReader) {
			LOGGER.debug("Returining reader : " + mailReader.getClass());
		}
		return mailReader;
	}

	private BaseMailReader createInstructionsReader(String subject) {
		BaseMailReader mailReader = invalidInstructions;
		if (subject.toLowerCase().contains("jira")) {
			mailReader = jiraInstructions;
		}
		return mailReader;
	}

	private BaseMailReader createSplunkReportReader(String subject) {
		BaseMailReader mailReader = null;
		if (subject.toLowerCase().contains("sync call")) {
			mailReader = syncCallReader;
		}
		return mailReader;
	}
}
