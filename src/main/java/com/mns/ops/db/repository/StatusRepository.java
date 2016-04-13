package com.mns.ops.db.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mns.ops.db.beans.Status;

public interface StatusRepository extends MongoRepository<Status, String> {
	public Status findByTitle(String title);
}
