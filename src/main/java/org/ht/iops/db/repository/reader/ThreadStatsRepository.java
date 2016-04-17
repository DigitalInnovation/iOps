package org.ht.iops.db.repository.reader;

import java.util.List;

import org.bson.types.ObjectId;
import org.ht.iops.db.beans.reader.ThreadStats;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ThreadStatsRepository
		extends
			MongoRepository<ThreadStats, ObjectId> {
	public List<ThreadStats> findByHost(String host);

	public List<ThreadStats> findByHostAndAboveThreshold(String host,
			String aboveThreshold);
}
