package com.mns.ops.db.beans;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cpuStats")
public class CPUStats {
	@Id
	private ObjectId id;
	private Date processingTime;
	private Date recordTime;
	private Double value;
	private String host;
	private String hall;
	private boolean aboveThreshold;

	/**
	 * @return the aboveThreshold
	 */
	public boolean isAboveThreshold() {
		return aboveThreshold;
	}

	/**
	 * @param aboveThreshold
	 *            the aboveThreshold to set
	 */
	public void setAboveThreshold(boolean aboveThreshold) {
		this.aboveThreshold = aboveThreshold;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Double value) {
		this.value = value;
	}

	/**
	 * @return the processingTime
	 */
	public Date getProcessingTime() {
		return processingTime;
	}

	/**
	 * @param processingTime
	 *            the processingTime to set
	 */
	public void setProcessingTime(Date processingTime) {
		this.processingTime = processingTime;
	}

	/**
	 * @return the recordTime
	 */
	public Date getRecordTime() {
		return recordTime;
	}

	/**
	 * @param recordTime
	 *            the recordTime to set
	 */
	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}

	/**
	 * @return the hall
	 */
	public String getHall() {
		return hall;
	}

	/**
	 * @param hall
	 *            the hall to set
	 */
	public void setHall(String hall) {
		this.hall = hall;
	}

	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(ObjectId id) {
		this.id = id;
	}
}