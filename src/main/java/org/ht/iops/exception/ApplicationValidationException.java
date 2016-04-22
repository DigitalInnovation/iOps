package org.ht.iops.exception;

import java.util.ArrayList;
import java.util.List;

public class ApplicationValidationException extends RuntimeException {
	private static final long serialVersionUID = 7882297404374307198L;
	private final String type;
	private List<String> errors;

	public ApplicationValidationException(final List<String> errors,
			final String type) {
		super(errors.get(0));
		this.type = type;
		this.errors = errors;
	}

	public ApplicationValidationException(final String error,
			final String type) {
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
