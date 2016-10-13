package org.ht.iops.db.repository;

import org.bson.types.ObjectId;
import org.ht.iops.db.beans.JobConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JobConfigRepository
		extends
			MongoRepository<JobConfig, ObjectId> {
	public JobConfig findByJobId(final String jobId);
}
