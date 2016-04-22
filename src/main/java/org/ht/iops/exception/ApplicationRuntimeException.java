package org.ht.iops.exception;

import java.util.ArrayList;
import java.util.List;

public class ApplicationRuntimeException extends RuntimeException {
	/**
	 */
	private static final long serialVersionUID = 7701956547062507854L;

	private String type;
	private List<String> errors;

	public ApplicationRuntimeException(final List<String> errors,
			final String type, final Throwable throwable) {
		super(throwable);
		this.errors = errors;
		this.type = type;
	}

	public ApplicationRuntimeException(final String type,
			final Throwable throwable, final String error) {
		this(null, type, throwable);
		this.errors = new ArrayList<>();
		this.errors.add(error);
	}

	public ApplicationRuntimeException(final String type,
			final Throwable throwable) {
		this(null, type, throwable);
		this.errors = new ArrayList<>();
		errors.add("An exception occured while parsing email message.");
	}

	public ApplicationRuntimeException(final List<String> errors,
			final String type) {
		super(errors.get(0));
		this.errors = errors;
		this.type = type;
	}

	public ApplicationRuntimeException(final String error, final String type) {
		super(error);
		this.errors = new ArrayList<>();
		this.errors.add(error);
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the errors
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * @param errors
	 *            the errors to set
	 */
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
}
