package com.mns.ops.db.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mns.ops.db.beans.CPUStats;

public interface CPUStatsRepository
		extends
			MongoRepository<CPUStats, ObjectId> {
	public List<CPUStats> findByHost(String host);

	public List<CPUStats> findByHostAndAboveThreshold(String host,
			String aboveThreshold);
}
