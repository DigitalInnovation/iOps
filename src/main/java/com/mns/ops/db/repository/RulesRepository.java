package com.mns.ops.db.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mns.ops.db.beans.Rules;

public interface RulesRepository extends MongoRepository<Rules, ObjectId> {
	public List<Rules> findByType(String type);

	public Rules findByTypeAndName(String type, String name);
}
