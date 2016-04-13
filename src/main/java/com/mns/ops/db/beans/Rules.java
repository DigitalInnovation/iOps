package com.mns.ops.db.beans;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Rules {
	@Id
	private ObjectId id;
	private String type;
	private String name;
	private Integer value;
	private String svalue;
	private Integer threshold;
	private String sthreshold;
	private String description;

	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the threshold
	 */
	public Integer getThreshold() {
		return threshold;
	}

	/**
	 * @return the sthreshold
	 */
	public String getSthreshold() {
		return sthreshold;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the value
	 */
	public Integer getValue() {
		return value;
	}

	/**
	 * @return the svalue
	 */
	public String getSvalue() {
		return svalue;
	}
}
