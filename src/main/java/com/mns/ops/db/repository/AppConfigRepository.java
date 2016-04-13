package com.mns.ops.db.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mns.ops.db.beans.AppConfig;

public interface AppConfigRepository
		extends
			MongoRepository<AppConfig, ObjectId> {
	public AppConfig findByNameAndType(final String name, final String type);
}
