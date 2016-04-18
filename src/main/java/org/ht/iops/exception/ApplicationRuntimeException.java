package org.ht.iops.exception;

public class ApplicationRuntimeException extends RuntimeException {
	/**
	 */
	private static final long serialVersionUID = 7701956547062507854L;

	private String customMessage;
	private String errorMessage;
	private String type;

	public ApplicationRuntimeException(final String message, final String type,
			Throwable throwable) {
		super(throwable);
		this.customMessage = message;
		this.errorMessage = throwable.getMessage();
		this.type = type;
	}

	public ApplicationRuntimeException(final String type,
			final Throwable throwable) {
		this("An exception occured while parsing email message.", type,
				throwable);
	}

	public ApplicationRuntimeException(final String message,
			final String type) {
		super(message);
		this.type = type;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	public String printMessage() {
		return this.getMessage() + this.errorMessage;
	}

	/**
	 * @return the customMessage
	 */
	public String getCustomMessage() {
		return customMessage;
	}
}
