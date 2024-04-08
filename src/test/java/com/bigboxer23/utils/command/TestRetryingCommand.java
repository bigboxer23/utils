package com.bigboxer23.utils.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import org.junit.jupiter.api.Test;

/** */
public class TestRetryingCommand {
	@Test
	public void testExecute() throws IOException {
		// Single success
		Integer[] countHolder = {0};
		RetryingCommand.execute(
				() -> {
					countHolder[0] = countHolder[0] + 1;
					return null;
				},
				"test");
		assertEquals(1, countHolder[0]);

		// One failure, then success
		countHolder[0] = 0;
		RetryingCommand.execute(
				() -> {
					if (countHolder[0] == 0) {
						countHolder[0] = countHolder[0] + 1;
						throw new IOException("bad");
					}
					countHolder[0] = countHolder[0] + 1;
					return null;
				},
				"test",
				0);
		assertEquals(2, countHolder[0]);

		// All failure, then exception
		countHolder[0] = 0;
		try {
			RetryingCommand.execute(
					() -> {
						countHolder[0] = countHolder[0] + 1;
						throw new IOException("bad");
					},
					"test",
					0);
			fail();
		} catch (IOException e) {
			assertEquals(2, countHolder[0]);
		}

		// A few failures, then success
		countHolder[0] = 0;
		RetryingCommand.execute(
				() -> {
					if (countHolder[0] < 2) {
						countHolder[0] = countHolder[0] + 1;
						throw new IOException("bad");
					}
					countHolder[0] = countHolder[0] + 1;
					return null;
				},
				"test",
				0,
				3);
		assertEquals(3, countHolder[0]);

		// All failure, then exception
		countHolder[0] = 0;
		try {
			RetryingCommand.execute(
					() -> {
						countHolder[0] = countHolder[0] + 1;
						throw new IOException("bad");
					},
					"test",
					0,
					3);
			fail();
		} catch (IOException e) {
			assertEquals(4, countHolder[0]);
		}
	}
}
