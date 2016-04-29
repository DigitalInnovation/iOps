package org.ht.iops.framework.mail.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.ht.iops.framework.mail.reader.instruction.InvalidInstructions;
import org.ht.iops.framework.mail.reader.instruction.JiraInstructions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class MailReaderFactoryTest {
	private MailReaderFactory readerFactory;
	@Mock
	private CPUStatsReader cpuStatsReader;
	@Mock
	private ThreadStatsReader threadStatsReader;
	@Mock
	private JiraInstructions jiraInstructions;
	@Mock
	private InvalidInstructions invalidInstructions;
	@Mock
	private SyncCallReader syncCallReader;
	@Mock
	private MimeMessage message;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() {
		readerFactory = new MailReaderFactory(cpuStatsReader, threadStatsReader,
				jiraInstructions, invalidInstructions, syncCallReader);
	}

	@Test
	public void getReader_SynCallReader() throws MessagingException {
		when(message.getSubject()).thenReturn(
				"Ecom Splunk Alert | Major | Hourly | Get Order List - Sync Call");
		assertThat(readerFactory.getReader(message)).isNotNull()
				.isExactlyInstanceOf(syncCallReader.getClass());
	}

	@Test
	public void getReader_JiraInstructionReader() throws MessagingException {
		when(message.getSubject()).thenReturn("iOps | Jira | Test Summary");
		assertThat(readerFactory.getReader(message)).isNotNull()
				.isExactlyInstanceOf(jiraInstructions.getClass());
	}

	@Test
	public void getReader_InvalidInstructionReader() throws MessagingException {
		when(message.getSubject()).thenReturn("iOps | Invalid | Test");
		assertThat(readerFactory.getReader(message)).isNotNull()
				.isExactlyInstanceOf(invalidInstructions.getClass());
	}
}
