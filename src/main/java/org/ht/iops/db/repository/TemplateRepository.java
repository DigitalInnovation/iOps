package org.ht.iops.db.repository;

import org.bson.types.ObjectId;
import org.ht.iops.db.beans.Template;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TemplateRepository
		extends
			MongoRepository<Template, ObjectId> {
	public Template findByNameAndType(final String name, final String type);
}
