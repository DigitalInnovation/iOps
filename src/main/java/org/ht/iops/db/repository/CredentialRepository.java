package org.ht.iops.db.repository;

import org.bson.types.ObjectId;
import org.ht.iops.db.beans.Credentials;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CredentialRepository
		extends
			MongoRepository<Credentials, ObjectId> {
	public Credentials findByType(String type);
}
