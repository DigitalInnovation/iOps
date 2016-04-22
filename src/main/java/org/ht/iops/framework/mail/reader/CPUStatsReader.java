package org.ht.iops.framework.mail.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.ht.iops.db.beans.Rules;
import org.ht.iops.db.beans.Status;
import org.ht.iops.db.beans.reader.CPUStats;
import org.ht.iops.db.repository.RulesRepository;
import org.ht.iops.db.repository.reader.CPUStatsRepository;
import org.ht.iops.exception.ApplicationException;
import org.ht.iops.exception.ApplicationRuntimeException;
import org.ht.iops.framework.mail.MailConstants;
import org.ht.iops.framework.mail.MailData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("cpuStatsReader")
public class CPUStatsReader extends BaseMailReader {
	private final Logger LOGGER = LoggerFactory.getLogger(CPUStatsReader.class);
	protected final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);

	@Autowired
	private RulesRepository rulesRepository;

	@Autowired
	private CPUStatsRepository cpuStatsRepository;

	@Override
	protected Status postProcess(final MimeMessage message,
			final MailData mailData) throws ApplicationException {
		Status status = null;
		try {
			if (mailData.getAttachments().size() == 1) {
				status = processAttachments(mailData.getAttachments().get(0),
						mailData.getSubject());
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
		return status;
	}

	protected Status processAttachments(final DataSource dataSource,
			final String subject) throws IOException {
		Status status = null;
		final Reader reader = new BufferedReader(
				new InputStreamReader(dataSource.getInputStream()));
		String hall, csvFileFormat[];
		if (subject.contains("H5")) {
			hall = "H5";
			csvFileFormat = setHeadersAndFormat(true);
		} else {
			hall = "H8";
			csvFileFormat = setHeadersAndFormat(false);
		}
		final List<String> headers = Arrays.asList(csvFileFormat);
		CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(csvFileFormat);
		CSVParser csvFileParser = new CSVParser(reader, csvFormat);
		Date processingTime = new Date();
		List<CSVRecord> records = csvFileParser.getRecords();
		records.remove(0);
		Map<String, CPUStats> cpuRecords = new HashMap<>();
		records.stream().forEach(p -> processCSVRecord(p, hall, headers,
				processingTime, cpuRecords));
		List<? extends CPUStats> stats = getStatsList(cpuRecords);
		status = checkRules(stats);
		csvFileParser.close();
		reader.close();
		return status;
	}

	protected List<? extends CPUStats> getStatsList(
			final Map<String, CPUStats> cpuRecords) {
		List<CPUStats> stats = new ArrayList<>();
		cpuRecords.entrySet().stream()
				.forEach(p -> stats.add(cpuRecords.get(p)));
		cpuStatsRepository.save(stats);
		return stats;
	}

	protected String[] setHeadersAndFormat(final boolean hall5) {
		String[] csvFileFormat;
		if (hall5) {
			csvFileFormat = MailConstants.CPU_USAGE_HEADER_MAPPING_H5;
		} else {
			csvFileFormat = MailConstants.CPU_USAGE_HEADER_MAPPING_H8;
		}
		return csvFileFormat;
	}

	protected void processCSVRecord(final CSVRecord record, final String hall,
			final List<String> headers, final Date processingTime,
			final Map<String, CPUStats> cpuRecords)
			throws ApplicationRuntimeException {
		try {
			Date timeStamp = DATE_FORMAT
					.parse(record.get("_time").replace(" BST", ""));
			headers.stream()
					.filter(p -> !"_time".equals(p)
							&& !StringUtils.isEmpty(record.get(p)))
					.forEach(p -> parseRecordForData(p, record.get(p),
							timeStamp, hall, processingTime, cpuRecords));
		} catch (ParseException exception) {
			throw new ApplicationRuntimeException(getReportName(), exception,
					"Exception occured while parsing Date");
		}
	}

	protected void parseRecordForData(final String headerString,
			final String value, final Date timeStamp, final String hall,
			final Date processingTime, final Map<String, CPUStats> cpuRecords) {
		createStatsObject(headerString, value, timeStamp, hall, processingTime,
				cpuRecords);
	}

	protected void createStatsObject(final String headerString,
			final String value, final Date timeStamp, final String hall,
			final Date processingTime, final Map<String, CPUStats> cpuRecords) {
		CPUStats cpuStats = null;
		String host = headerString.replaceAll(".att.mnscorp.net", "")
				.replaceAll("s220823vaps", "").replaceAll("s221533vaps", "");
		if (cpuRecords.containsKey(host)) {
			cpuStats = cpuRecords.get(host);
		} else {
			cpuStats = new CPUStats();
			cpuStats.setProcessingTime(processingTime);
			cpuRecords.put(host, cpuStats);
			cpuStats.setHost(host);
			cpuStats.setHall(hall);
		}
		cpuStats.setRecordTime(timeStamp);
		cpuStats.setValue(Double.valueOf(value));
	}

	protected Status checkRules(final List<? extends CPUStats> stats) {
		Map<String, Rules> rules = getRules(getReportName());
		Rules amber = rules.get("AMBER");
		Rules red = rules.get("RED");
		List<String> ambers = new ArrayList<>();
		List<String> reds = new ArrayList<>();
		String desciption = getDescription(0);
		String status = "Green";
		String threshold = "";
		stats.stream().filter(p -> p.getValue() > amber.getValue())
				.forEach(p -> ambers.add(p.getHost()));
		stats.stream().filter(p -> p.getValue() > red.getValue())
				.forEach(p -> reds.add(p.getHost()));
		if (reds.size() > red.getThreshold()) {
			desciption = getDescription(2);
			desciption = getStatusDescription(reds);
			status = "Red";
			threshold = red.getDescription();
			LOGGER.info("Status RED " + desciption);
		} else if (ambers.size() > amber.getThreshold()) {
			desciption = getDescription(1);
			desciption = getStatusDescription(ambers);
			status = "Amber";
			threshold = amber.getDescription();
			LOGGER.info("Status AMBER " + desciption);
		}
		return getStatus(status, desciption, threshold);
	}

	protected String getStatusDescription(final List<String> stats) {
		StringBuffer description = new StringBuffer();
		stats.stream().forEach(p -> {
			description.append(p + ",");
		});
		return description.substring(0, description.length() - 2);
	}

	protected String getDescription(final int status) {
		String desciption = "";
		if (status > 1) {
			desciption = "Following servers are running at red -> ";
		} else if (status > 0) {
			desciption = "Following servers are running at amber -> ";
		} else {
			desciption = "All servers are running below threshold.";
		}
		return desciption;
	}

	protected Map<String, Rules> getRules(String name) {
		Map<String, Rules> rules = new HashMap<>();
		List<Rules> dbRules = rulesRepository.findByType(name);
		dbRules.stream().forEach(p -> rules.put(p.getName(), p));
		return rules;
	}

	@Override
	public Status getStatus(String... strings) {
		Status status = new Status(getReportTitle(), strings[0]);
		status.setLastUpdate(new Date());
		status.setUrl(getReportName());
		status.setType("keyValue");
		status.setSection("serverstats");
		status.setReason(strings[1]);
		status.setOverview(strings[2]);
		LOGGER.debug("status : " + status);
		return status;
	}

	protected String getReportName() {
		return "cpuusage";
	}

	protected String getReportTitle() {
		return "CPU Usage";
	}
}