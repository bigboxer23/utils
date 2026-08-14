package com.bigboxer23.utils.command;

import java.io.IOException;

/**
 * Thrown by a {@link Command} when the failure will not be fixed by trying again, so {@link
 * RetryingCommand} should give up immediately instead of burning its retry budget.
 *
 * <p>The motivating case is an API rate limit: retrying a rejected call turns one request into
 * several and makes the limit harder to recover from.
 */
public class NonRetryableException extends IOException {
	public NonRetryableException(String message) {
		super(message);
	}

	public NonRetryableException(String message, Throwable cause) {
		super(message, cause);
	}
}
