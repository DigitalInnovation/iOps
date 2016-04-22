package org.ht.iops.db.repository;

import org.bson.types.ObjectId;
import org.ht.iops.db.beans.SlackConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SlackConfigRepository
		extends
			MongoRepository<SlackConfig, ObjectId> {
	public SlackConfig findByNameAndListnerAndType(final String name,
			final String listner, final String type);
}
