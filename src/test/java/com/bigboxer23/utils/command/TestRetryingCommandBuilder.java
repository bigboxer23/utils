package com.bigboxer23.utils.command;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import org.junit.jupiter.api.Test;

public class TestRetryingCommandBuilder {

	@Test
	void testDefaultValues() throws IOException {
		Command<String> command = () -> "success";
		RetryingCommandBuilder builder = new RetryingCommandBuilder();
		String result = builder.buildAndExecute(command);
		assertEquals("success", result);
	}

	@Test
	void testSetIdentifier() throws IOException {
		Command<String> command = () -> "success";
		RetryingCommandBuilder builder = new RetryingCommandBuilder();
		String result = builder.identifier("test-id").buildAndExecute(command);
		assertEquals("success", result);
	}

	@Test
	void testSetWaitInSeconds() throws IOException {
		Command<String> command = () -> "success";
		RetryingCommandBuilder builder = new RetryingCommandBuilder();
		String result = builder.waitInSeconds(5).buildAndExecute(command);
		assertEquals("success", result);
	}

	@Test
	void testSetNumberOfRetriesBeforeFailure() throws IOException {
		Command<String> command = () -> "success";
		RetryingCommandBuilder builder = new RetryingCommandBuilder();
		String result = builder.numberOfRetriesBeforeFailure(3).buildAndExecute(command);
		assertEquals("success", result);
	}

	@Test
	void testSetFailureCommand() throws IOException {
		Command<String> command = () -> "success";
		VoidCommand failureCommand = mock(VoidCommand.class);
		RetryingCommandBuilder builder = new RetryingCommandBuilder();
		String result = builder.failureCommand(failureCommand).buildAndExecute(command);
		assertEquals("success", result);
		verifyNoInteractions(failureCommand);
	}

	@Test
	void testChainedCalls() throws IOException {
		Command<String> command = () -> "success";
		VoidCommand failureCommand = mock(VoidCommand.class);
		RetryingCommandBuilder builder = new RetryingCommandBuilder();

		String result = builder.identifier("chain-test")
				.waitInSeconds(2)
				.numberOfRetriesBeforeFailure(2)
				.failureCommand(failureCommand)
				.buildAndExecute(command);

		assertEquals("success", result);
		verifyNoInteractions(failureCommand);
	}

	@Test
	void testZeroRetriesThrowsException() {
		RetryingCommandBuilder builder = new RetryingCommandBuilder();
		IllegalArgumentException exception =
				assertThrows(IllegalArgumentException.class, () -> builder.numberOfRetriesBeforeFailure(0));
		assertEquals("Number of retries before failure is zero.", exception.getMessage());
	}

	@Test
	void testBuilderReturnsCorrectType() {
		RetryingCommandBuilder builder = new RetryingCommandBuilder();
		assertSame(builder, builder.identifier("test"));
		assertSame(builder, builder.waitInSeconds(5));
		assertSame(builder, builder.numberOfRetriesBeforeFailure(3));
		assertSame(builder, builder.failureCommand(() -> {}));
	}

	@Test
	void testNullFailureCommand() throws IOException {
		Command<String> command = () -> "success";
		RetryingCommandBuilder builder = new RetryingCommandBuilder();
		String result = builder.failureCommand(null).buildAndExecute(command);
		assertEquals("success", result);
	}

	@Test
	void testNullIdentifier() throws IOException {
		Command<String> command = () -> "success";
		RetryingCommandBuilder builder = new RetryingCommandBuilder();
		String result = builder.identifier(null).buildAndExecute(command);
		assertEquals("success", result);
	}
}
