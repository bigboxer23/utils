package com.bigboxer23.utils.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** */
public class TestFilePersistedString {
	@Test
	void testFilePersistedStringSetAndGet() {
		FilePersistedString str = new FilePersistedString("testString");
		str.set("Hello\nWorld");
		FilePersistedString loaded = new FilePersistedString("testString");
		assertEquals("Hello\nWorld", loaded.get());
	}
}
