package com.bigboxer23.utils.command;

import com.bigboxer23.utils.logging.LoggingUtil;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Run a command and retry it once after a pause */
public class RetryingCommand {
	private static final Logger logger = LoggerFactory.getLogger(RetryingCommand.class);

	/**
	 * Run a command and retry it after a pause. Can define pause length in seconds, and number of
	 * retry attempts
	 *
	 * @param command command to run
	 * @param identifier identifier to log on failures
	 * @param waitInSeconds number of seconds to pause
	 * @param numberOfRetriesBeforeFailure how many times to retry
	 * @return Command's return value
	 * @param <T>
	 * @throws IOException
	 */
	public static <T> T execute(
			Command<T> command,
			String identifier,
			long waitInSeconds,
			int numberOfRetriesBeforeFailure,
			VoidCommand failureCommand)
			throws IOException {
		try {
			logger.debug("Starting command " + identifier);
			return command.execute();
		} catch (IOException e) {
			for (int ai = 0; ai < numberOfRetriesBeforeFailure; ai++) {
				logger.error("error running command, attempting to retry " + ai + " " + identifier, e);
				try {
					Thread.sleep(waitInSeconds * 1000); // 5 sec
					return command.execute();
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

	public static RetryingCommandBuilder builder() {
		return new RetryingCommandBuilder();
	}
}
