package com.bigboxer23.utils.command;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Run a command and retry it once after a pause */
public class RetryingCommand {
	private static final Logger logger = LoggerFactory.getLogger(RetryingCommand.class);

	public static <T> T execute(Command<T> command, String identifier) throws IOException {
		try {
			logger.info("Starting command " + identifier);
			return command.execute();
		} catch (IOException e) {
			logger.error("error running command, attempting to retry " + identifier, e);
			try {
				Thread.sleep(5 * 1000); // 5 sec
				return command.execute();
			} catch (IOException e2) {
				logger.error("error retrying command " + identifier, e2);
				throw e2;
			} catch (InterruptedException e2) {
				logger.error("error retrying command " + identifier, e2);
			}
		}
		return null;
	}
}
