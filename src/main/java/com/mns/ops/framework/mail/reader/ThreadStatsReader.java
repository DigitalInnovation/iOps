package com.mns.ops.framework.mail.reader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mns.ops.db.beans.CPUStats;
import com.mns.ops.db.beans.ThreadStats;
import com.mns.ops.db.repository.ThreadStatsRepository;
import com.mns.ops.framework.mail.MailConstants;

@Component
public class ThreadStatsReader extends CPUStatsReader {
	@Autowired
	private ThreadStatsRepository threadStatsRepository;

	@Override
	protected String[] setHeadersAndFormat(final boolean hall5) {
		String[] csvFileFormat;
		if (hall5) {
			csvFileFormat = MailConstants.THREAD_COUNT_HEADER_MAPPING_H5;
		} else {
			csvFileFormat = MailConstants.THREAD_COUNT_HEADER_MAPPING_H8;
		}
		return csvFileFormat;
	}

	@Override
	protected void createStatsObject(final String headerString,
			final String value, final Date timeStamp, final String hall,
			final Date processingTime, final Map<String, CPUStats> cpuRecords) {
		CPUStats threadStats = null;
		String host = headerString;
		if (cpuRecords.containsKey(host)) {
			threadStats = cpuRecords.get(host);
		} else {
			threadStats = new ThreadStats();
			threadStats.setProcessingTime(processingTime);
			cpuRecords.put(host, threadStats);
			threadStats.setHost(host);
			threadStats.setHall(hall);
		}
		threadStats.setRecordTime(timeStamp);
		threadStats.setValue(Double.valueOf(value));
	}

	protected List<? extends CPUStats> getStatsList(
			final Map<String, CPUStats> cpuRecords) {
		List<ThreadStats> stats = new ArrayList<>();
		cpuRecords.entrySet().stream()
				.forEach(p -> stats.add((ThreadStats) cpuRecords.get(p)));
		threadStatsRepository.save(stats);
		return stats;
	}

	@Override
	protected String getReportName() {
		return "cputhreadcount";
	}

	@Override
	protected String getReportTitle() {
		return "JVM Thread Count";
	}
}
