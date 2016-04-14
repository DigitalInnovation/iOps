package org.ht.iops.db.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.ht.iops.db.beans.CPUStats;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CPUStatsRepository
		extends
			MongoRepository<CPUStats, ObjectId> {
	public List<CPUStats> findByHost(String host);

	public List<CPUStats> findByHostAndAboveThreshold(String host,
			String aboveThreshold);
}
