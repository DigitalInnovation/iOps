package org.ht.iops.db.repository;

import org.bson.types.ObjectId;
import org.ht.iops.db.beans.AppConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppConfigRepository
		extends
			MongoRepository<AppConfig, ObjectId> {
	public AppConfig findByNameAndType(final String name, final String type);
}
