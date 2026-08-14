package com.bigboxer23.utils.command;

import com.bigboxer23.utils.logging.LoggingUtil;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Run a command and retry it after a pause, backing off between attempts */
public class RetryingCommand {
	private static final Logger logger = LoggerFactory.getLogger(RetryingCommand.class);

	public static final long DEFAULT_MAX_WAIT_IN_SECONDS = 60;

	/**
	 * @see #execute(Command, String, long, long, int, VoidCommand)
	 */
	public static <T> T execute(
			Command<T> command,
			String identifier,
			long waitInSeconds,
			int numberOfRetriesBeforeFailure,
			VoidCommand failureCommand)
			throws IOException {
		return execute(
				command,
				identifier,
				waitInSeconds,
				DEFAULT_MAX_WAIT_IN_SECONDS,
				numberOfRetriesBeforeFailure,
				failureCommand);
	}

	/**
	 * Run a command and retry it after a pause. Can define pause length in seconds, and number of
	 * retry attempts
	 *
	 * <p>The pause doubles after each failed attempt, up to maxWaitInSeconds, with jitter applied.
	 *
	 * <p>A command that throws {@link NonRetryableException} is not retried, the exception is
	 * rethrown as-is.
	 *
	 * @param command command to run
	 * @param identifier identifier to log on failures
	 * @param waitInSeconds number of seconds to pause before the first retry
	 * @param maxWaitInSeconds ceiling for a single pause once the backoff has grown
	 * @param numberOfRetriesBeforeFailure how many times to retry
	 * @return Command's return value
	 * @param <T>
	 * @throws IOException
	 */
	public static <T> T execute(
			Command<T> command,
			String identifier,
			long waitInSeconds,
			long maxWaitInSeconds,
			int numberOfRetriesBeforeFailure,
			VoidCommand failureCommand)
			throws IOException {
		try {
			logger.debug("Starting command " + identifier);
			return command.execute();
		} catch (NonRetryableException e) {
			logger.error("command failed and will not be retried " + identifier, e);
			throw e;
		} catch (IOException e) {
			for (int ai = 0; ai < numberOfRetriesBeforeFailure; ai++) {
				logger.error("error running command, attempting to retry " + ai + " " + identifier, e);
				try {
					Thread.sleep(backoffMillis(waitInSeconds, maxWaitInSeconds, ai));
					return command.execute();
				} catch (NonRetryableException e2) {
					logger.error("command failed and will not be retried further " + identifier, e2);
					throw e2;
				} catch (IOException e2) {
					logger.error("error retrying command " + ai + " " + identifier, e2);
					if (ai + 1 == numberOfRetriesBeforeFailure) {
						throw e2;
					}
				} catch (InterruptedException e2) {
					logger.error("error retrying command " + identifier, e2);
					if (failureCommand != null) {
						try {
							failureCommand.execute();
						} catch (InterruptedException e3) {
							logger.error("error failure command " + identifier, e3);
						}
					}
				}
			}
		} finally {
			LoggingUtil.clearDeviceId();
		}
		return null;
	}

	/**
	 * Delay before the given retry attempt: waitInSeconds doubled once per prior attempt, capped at
	 * maxWaitInSeconds, then jittered.
	 *
	 * <p>Jitter keeps half the delay fixed and randomizes the rest, so the backoff still grows
	 * predictably while separating callers that failed at the same moment.
	 *
	 * @param waitInSeconds pause before the first retry
	 * @param maxWaitInSeconds ceiling for a single pause, ignored when not positive
	 * @param attempt zero based retry number
	 * @return milliseconds to sleep
	 */
	protected static long backoffMillis(long waitInSeconds, long maxWaitInSeconds, int attempt) {
		if (waitInSeconds <= 0) {
			return 0;
		}
		long ceiling = maxWaitInSeconds > 0 ? maxWaitInSeconds : Long.MAX_VALUE / 1000;
		long delay = Math.min(waitInSeconds, ceiling);
		for (int ai = 0; ai < attempt && delay < ceiling; ai++) {
			delay = Math.min(delay * 2, ceiling);
		}
		long delayMillis = delay * 1000;
		long half = delayMillis / 2;
		return half + ThreadLocalRandom.current().nextLong(delayMillis - half + 1);
	}

	public static RetryingCommandBuilder builder() {
		return new RetryingCommandBuilder();
	}
}
