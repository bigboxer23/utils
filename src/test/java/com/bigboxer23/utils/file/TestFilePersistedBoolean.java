package com.bigboxer23.utils.file;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** */
public class TestFilePersistedBoolean {
	private static final String TEST_FILE = "testBoolean";

	@BeforeAll
	public static void classSetup() {
		new FilePersistedBoolean(TEST_FILE).set(false);
	}

	@Test
	public void test() {
		FilePersistedBoolean test = new FilePersistedBoolean(TEST_FILE);
		assertFalse(test.get());
		test.set(true);
		assertTrue(test.get());
		test.set(false);
		assertFalse(test.get());
		test.set(true);

		test = new FilePersistedBoolean(TEST_FILE);
		assertTrue(test.get());
	}
}