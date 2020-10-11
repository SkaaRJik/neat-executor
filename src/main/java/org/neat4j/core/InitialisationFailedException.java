package org.neat4j.core;

/**
 * @author MSimmerson
 *
 */
public class InitialisationFailedException extends Exception {

	public InitialisationFailedException() {
	}

	public InitialisationFailedException(String message) {
		super(message);
	}

	public InitialisationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public InitialisationFailedException(Throwable cause) {
		super(cause);
	}
}
