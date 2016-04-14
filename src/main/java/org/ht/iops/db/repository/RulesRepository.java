package org.ht.iops.db.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.ht.iops.db.beans.Rules;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RulesRepository extends MongoRepository<Rules, ObjectId> {
	public List<Rules> findByType(String type);

	public Rules findByTypeAndName(String type, String name);
}
