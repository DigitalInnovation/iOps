package org.ht.iops.framework.mail.reader.instruction;

import static org.springframework.util.StringUtils.hasText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.ht.iops.db.beans.Status;
import org.ht.iops.db.repository.StatusRepository;
import org.ht.iops.events.IOpsEvent;
import org.ht.iops.events.publisher.EventPublisher;
import org.ht.iops.exception.ApplicationException;
import org.ht.iops.exception.ApplicationRuntimeException;
import org.ht.iops.exception.ApplicationValidationException;
import org.ht.iops.framework.mail.MailData;
import org.ht.iops.framework.mail.reader.BaseMailReader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class BulkJira extends BaseMailReader {

	public BulkJira(final StatusRepository statusRepository,
			final EventPublisher eventPublisher) {
		super(statusRepository, eventPublisher);
	}

	@Override
	protected Status postProcess(MimeMessage message, MailData mailData)
			throws ApplicationException {
		try {
			if (mailData.getAttachments().size() == 1) {
				processAttachments(mailData.getAttachments().get(0), mailData);
			} else {
				throw new ApplicationException(
						"Error parsing email, email attachment not found.",
						getReportName(), new InvalidParameterException(
								"Error parsing email, email attachment not found."));
			}
		} catch (IOException ioException) {
			throw new ApplicationException(
					"IO Exception occured while parsing attachment.",
					getReportName(), ioException);
		} catch (ApplicationRuntimeException runtimeException) {
			throw new ApplicationException(runtimeException);
		}
		return null;
	}

	protected Status processAttachments(final DataSource dataSource,
			final MailData mailData) throws IOException {
		Status status = null;
		final Reader reader = new BufferedReader(
				new InputStreamReader(dataSource.getInputStream()));
		String csvFileFormat[] = {"Summary", "Description", "Worktype", "Owner",
				"Estimate", "Labels"};
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(csvFileFormat);
		CSVParser csvFileParser = new CSVParser(reader, csvFormat);
		List<CSVRecord> records = csvFileParser.getRecords();
		records.remove(0);
		records.stream().forEach(p -> processCSVRecord(p, mailData));
		csvFileParser.close();
		reader.close();
		return status;
	}

	protected void processCSVRecord(final CSVRecord record,
			final MailData mailData) throws ApplicationRuntimeException {
		Map<String, String> attributes = new HashMap<>();
		populateAttributes(record, attributes);
		try {
			if (validRecord(attributes))
				eventPublisher.createEvent(createEvent(attributes, mailData));
		} catch (ApplicationValidationException validationException) {
			validationException.printStackTrace();
		}
	}

	private boolean validRecord(Map<String, String> attributes) {
		boolean valid = false;
		if (hasText(attributes.get("summary"))
				&& hasText(attributes.get("description"))
				&& hasText(attributes.get("worktype"))
				&& hasText(attributes.get("owner")))
			valid = true;
		return valid;
	}

	private void populateAttributes(final CSVRecord record,
			Map<String, String> attributes) {
		attributes.put("summary", record.get("Summary"));
		attributes.put("description", record.get("Description"));
		attributes.put("worktype", record.get("Worktype"));
		attributes.put("owner", record.get("Owner"));
		attributes.put("estimate", record.get("Estimate"));
		attributes.put("labels", record.get("Labels"));
	}

	protected IOpsEvent createEvent(final Map<String, String> attributes,
			final MailData mailData) {
		Assert.notNull(mailData, "Mail Data cannot be null.");
		IOpsEvent iOpsEvent = createEvent("jira", mailData);
		iOpsEvent.setAttributes(attributes);
		iOpsEvent.setResponseRequired(false);
		return iOpsEvent;
	}

	@Override
	protected String getReportName() {
		return "Bulk Jira";
	}
}
