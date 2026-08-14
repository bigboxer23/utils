package com.bigboxer23.utils.command;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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
	void testBackoffDoublesPerAttempt() {
		// jitter keeps half the delay fixed, so attempt n lands in [base * 2^n / 2, base * 2^n]
		assertBackoffBetween(10_000, 20_000, 10, 60, 1);
		assertBackoffBetween(20_000, 40_000, 10, 60, 2);
	}

	@Test
	void testBackoffIsCapped() {
		// without a cap this would be 10 * 2^10 seconds
		assertBackoffBetween(30_000, 60_000, 10, 60, 10);
	}

	@Test
	void testFirstRetryUsesConfiguredWait() {
		assertBackoffBetween(5_000, 10_000, 10, 60, 0);
	}

	@Test
	void testZeroWaitProducesNoBackoff() {
		for (int attempt = 0; attempt < 5; attempt++) {
			assertEquals(0, RetryingCommand.backoffMillis(0, 60, attempt));
		}
	}

	@Test
	void testBackoffIsJittered() {
		Set<Long> observed = new HashSet<>();
		for (int i = 0; i < 100; i++) {
			observed.add(RetryingCommand.backoffMillis(10, 60, 2));
		}
		assertTrue(observed.size() > 1, "expected jitter to vary the delay, always got " + observed);
	}

	private void assertBackoffBetween(long minMillis, long maxMillis, long wait, long maxWait, int attempt) {
		long actual = RetryingCommand.backoffMillis(wait, maxWait, attempt);
		assertTrue(
				actual >= minMillis && actual <= maxMillis,
				"attempt " + attempt + " backoff " + actual + " outside [" + minMillis + ", " + maxMillis + "]");
	}

	@Test
	void testNonRetryableIsNotRetried() {
		AtomicInteger counter = new AtomicInteger(0);
		NonRetryableException thrown = assertThrows(
				NonRetryableException.class,
				() -> RetryingCommand.builder()
						.identifier("non-retryable")
						.waitInSeconds(0)
						.numberOfRetriesBeforeFailure(5)
						.buildAndExecute(() -> {
							counter.incrementAndGet();
							throw new NonRetryableException("rate limited");
						}));
		assertEquals("rate limited", thrown.getMessage());
		assertEquals(1, counter.get());
	}

	@Test
	void testNonRetryableDuringRetryAbandonsRemainingAttempts() {
		AtomicInteger counter = new AtomicInteger(0);
		assertThrows(
				NonRetryableException.class,
				() -> RetryingCommand.builder()
						.identifier("non-retryable-on-retry")
						.waitInSeconds(0)
						.numberOfRetriesBeforeFailure(5)
						.buildAndExecute(() -> {
							if (counter.getAndIncrement() < 1) {
								throw new IOException("transient");
							}
							throw new NonRetryableException("rate limited");
						}));
		assertEquals(2, counter.get());
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
