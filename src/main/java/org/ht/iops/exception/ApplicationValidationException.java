package org.ht.iops.exception;

public class ApplicationValidationException extends RuntimeException {
	private static final long serialVersionUID = 7882297404374307198L;
	private final String type;

	public ApplicationValidationException(String message, String type) {
		super(message);
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
}
