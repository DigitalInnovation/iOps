package org.ht.iops.db.repository.config;

import org.bson.types.ObjectId;
import org.ht.iops.db.beans.config.EventConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventConfigRepository
		extends
			MongoRepository<EventConfig, ObjectId> {

}
