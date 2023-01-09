package com.nium.interview.transfers.exception;

/**
 * The Transfer RuntimeException
 * @author okezie okechukwu
 */
public class TransferException extends RuntimeException {

	/**
	 * @param message the message of exception
	 */
	public TransferException(final String message) {
		super(message);
	}

	/**
	 * @param message the message to wrap the caught exception
	 * @param cause the caught exception
	 */
	public TransferException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
