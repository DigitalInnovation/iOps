package org.ht.iops.framework.mail.reader;

import static org.springframework.util.StringUtils.startsWithIgnoreCase;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.ht.iops.framework.mail.reader.alert.PlatformAlert;
import org.ht.iops.framework.mail.reader.alert.SyncCallAlert;
import org.ht.iops.framework.mail.reader.incident.IncidentReader;
import org.ht.iops.framework.mail.reader.instruction.BulkJira;
import org.ht.iops.framework.mail.reader.instruction.InvalidInstructions;
import org.ht.iops.framework.mail.reader.instruction.JiraInstructions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MailReaderFactory {
	private final Logger LOGGER = LoggerFactory
			.getLogger(MailReaderFactory.class);

	private CPUStatsReader cpuStatsReader;
	private ThreadStatsReader threadStatsReader;
	private JiraInstructions jiraInstructions;
	private InvalidInstructions invalidInstructions;
	private SyncCallAlert syncCallReader;
	private IncidentReader incidentReader;
	private BulkJira bulkJira;
	private PlatformAlert alert;

	public MailReaderFactory(final CPUStatsReader cpuStatsReader,
			final ThreadStatsReader threadStatsReader,
			final JiraInstructions jiraInstructions,
			final InvalidInstructions invalidInstructions,
			final SyncCallAlert syncCallReader,
			final IncidentReader incidentReader, final BulkJira bulkJira,
			final PlatformAlert alert) {
		this.cpuStatsReader = cpuStatsReader;
		this.threadStatsReader = threadStatsReader;
		this.jiraInstructions = jiraInstructions;
		this.invalidInstructions = invalidInstructions;
		this.syncCallReader = syncCallReader;
		this.incidentReader = incidentReader;
		this.bulkJira = bulkJira;
		this.alert = alert;
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
			} else if (startsWithIgnoreCase(message.getSubject(), "iOps")) {
				mailReader = createInstructionsReader(message.getSubject());
			} else if (startsWithIgnoreCase(message.getSubject(),
					"Ecom Splunk Alert | Major")) {
				mailReader = createSplunkReportReader(message.getSubject());
			} else if (message.getSubject().contains("INC000")) {
				mailReader = incidentReader;
			} else if (message.getSubject().contains("MNS PLATFORM Alert")
					|| message.getSubject()
							.contains("M&S Control-M JOB alert")) {
				mailReader = alert;
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
		if (subject.toLowerCase().contains("bulkjira")) {
			mailReader = bulkJira;
		} else if (subject.toLowerCase().contains("jira")) {
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
