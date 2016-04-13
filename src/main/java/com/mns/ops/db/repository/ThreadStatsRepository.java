package com.mns.ops.db.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mns.ops.db.beans.ThreadStats;

public interface ThreadStatsRepository
		extends
			MongoRepository<ThreadStats, ObjectId> {
	public List<ThreadStats> findByHost(String host);

	public List<ThreadStats> findByHostAndAboveThreshold(String host,
			String aboveThreshold);
}
