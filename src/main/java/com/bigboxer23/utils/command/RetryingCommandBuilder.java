package com.bigboxer23.utils.command;

import java.io.IOException;

/** */
public class RetryingCommandBuilder {
	private String identifier = null;
	private long waitInSeconds = 10;
	private int numberOfRetriesBeforeFailure = 5;
	private VoidCommand failureCommand = null;

	public RetryingCommandBuilder identifier(String identifier) {
		this.identifier = identifier;
		return this;
	}

	public RetryingCommandBuilder waitInSeconds(long waitInSeconds) {
		this.waitInSeconds = waitInSeconds;
		return this;
	}

	public RetryingCommandBuilder numberOfRetriesBeforeFailure(int numberOfRetriesBeforeFailure) {
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
