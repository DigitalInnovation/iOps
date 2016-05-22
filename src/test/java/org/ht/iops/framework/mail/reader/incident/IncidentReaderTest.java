package org.ht.iops.framework.mail.reader.incident;

import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.ht.iops.db.beans.config.AppConfig;
import org.ht.iops.db.repository.config.AppConfigRepository;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.exception.ApplicationException;
import org.ht.iops.framework.mail.MailData;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class IncidentReaderTest {
	private IncidentReader incidentReader;

	private MailData mailData;
	@Mock
	private AppConfigRepository appConfigRepository;
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;
	@Mock
	private AppConfigRepository configRepository;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static String _incident1String = "";

	@BeforeClass
	public static void classSetup() throws IOException {
		InputStream _incident1 = new ClassPathResource(
				"message/templates/incident_1.txt").getInputStream();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(_incident1));
		String line = "";
		while ((line = reader.readLine()) != null)
			_incident1String = _incident1String.concat(line)
					.concat(System.lineSeparator());
		_incident1.close();
	}

	@AfterClass
	public static void classShutdown() {
		_incident1String = null;
	}

	@Before
	public void setUp() {
		incidentReader = new IncidentReader(null,
				new EventPublisher(applicationEventPublisher),
				configRepository);
		mailData = new MailData();
		mailData.setSubject(
				"Medium - INC000029493417 - eComm Platform Support : "
						+ "EMAIL-PROBE (Site Confidence Alert:Script Step[29142885]) - "
						+ "Site Confidence Alert Script Step F");
		mailData.setPlainContent(_incident1String);
		AppConfig config = new AppConfig();
		config.setValue(".comsupport");
		when(configRepository.findByNameAndType("queuename",
				"eComm Platform Support")).thenReturn(config);
	}

	@Test
	public void postProcess() throws ApplicationException {
		incidentReader.postProcess(null, mailData);
	}
}
