package com.bigboxer23.utils.command;

import java.io.IOException;

/** */
public class RetryingCommandBuilder {
	private String identifier = null;
	private long waitInSeconds = 10;
	private int numberOfRetriesBeforeFailure = 5;
	private VoidCommand failureCommand = null;

	RetryingCommandBuilder() {}

	public RetryingCommandBuilder identifier(String identifier) {
		this.identifier = identifier;
		return this;
	}

	public RetryingCommandBuilder waitInSeconds(long waitInSeconds) {
		this.waitInSeconds = waitInSeconds;
		return this;
	}

	public RetryingCommandBuilder numberOfRetriesBeforeFailure(int numberOfRetriesBeforeFailure) {
		if (numberOfRetriesBeforeFailure == 0) {
			throw new IllegalArgumentException("Number of retries before failure is zero.");
		}
		this.numberOfRetriesBeforeFailure = numberOfRetriesBeforeFailure;
		return this;
	}

	public RetryingCommandBuilder failureCommand(VoidCommand failureCommand) {
		this.failureCommand = failureCommand;
		return this;
	}

	public <T> T buildAndExecute(Command<T> command) throws IOException {
		return RetryingCommand.execute(
				command, identifier, waitInSeconds, numberOfRetriesBeforeFailure, failureCommand);
	}
}
