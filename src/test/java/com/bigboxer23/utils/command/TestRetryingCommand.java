package com.bigboxer23.utils.command;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** */
public class TestRetryingCommand {
	@ParameterizedTest
	@CsvSource({"0,0", "1,2", "3,4"})
	void testRetries(int maxRetries, int expectedCalls) {
		Integer[] countHolder = {0};
		try {
			RetryingCommand.builder()
					.identifier("param-test")
					.waitInSeconds(0)
					.numberOfRetriesBeforeFailure(maxRetries)
					.buildAndExecute(() -> {
						countHolder[0]++;
						throw new IOException("fail");
					});
			fail();
		} catch (IOException | IllegalArgumentException e) {
			assertEquals(expectedCalls, countHolder[0]);
		}
	}

	@Test
	void testRetryCommandSucceedsOnSecondTry() throws IOException {
		AtomicInteger counter = new AtomicInteger(0);
		String result = RetryingCommand.builder()
				.identifier("retry-success")
				.waitInSeconds(0)
				.numberOfRetriesBeforeFailure(2)
				.buildAndExecute(() -> {
					if (counter.getAndIncrement() < 1) throw new IOException("fail once");
					return "success";
				});
		assertEquals("success", result);
		assertEquals(2, counter.get());
	}
}
