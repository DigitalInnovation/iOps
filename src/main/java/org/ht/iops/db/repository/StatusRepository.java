package org.ht.iops.db.repository;

import org.ht.iops.db.beans.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatusRepository extends MongoRepository<Status, String> {
	public Status findByTitle(String title);
}
