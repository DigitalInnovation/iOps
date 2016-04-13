package com.mns.ops.db.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mns.ops.db.beans.Credentials;

public interface CredentialRepository
		extends
			MongoRepository<Credentials, ObjectId> {
	public Credentials findByType(String type);
}
