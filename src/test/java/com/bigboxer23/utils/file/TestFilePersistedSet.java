package com.bigboxer23.utils.file;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** */
public class TestFilePersistedSet {
	private static final String TEST_FILE = "testSet";

	private static final Collection<String> TEST_DATA = new ArrayList<String>() {
		{
			add("string1");
			add("string2");
			add("string\nmore complex\nstring");
		}
	};

	@BeforeAll
	public static void classSetup() {
		new FilePersistedSet(TEST_FILE).reset();
	}

	@Test
	public void testAdd() {
		FilePersistedSet fromMemory = new FilePersistedSet(TEST_FILE);
		TEST_DATA.forEach(fromMemory::add);
		FilePersistedSet fromFile = new FilePersistedSet(TEST_FILE);
		TEST_DATA.forEach(data -> assertTrue(fromMemory.contains(data)));
		assertFalse(fromFile.contains("string"));
		assertFalse(fromFile.contains("more complex"));
		TEST_DATA.forEach(data -> assertTrue(fromFile.contains(data)));
	}

	@Test
	public void testAddAll() {
		FilePersistedSet fromMemory = new FilePersistedSet(TEST_FILE);
		fromMemory.addAll(TEST_DATA);
		FilePersistedSet fromFile = new FilePersistedSet(TEST_FILE);
		TEST_DATA.forEach(data -> assertTrue(fromMemory.contains(data)));
		assertFalse(fromFile.contains("string"));
		assertFalse(fromFile.contains("more complex"));
		TEST_DATA.forEach(data -> assertTrue(fromFile.contains(data)));
	}

	@Test
	public void testDelete() {
		FilePersistedSet set = new FilePersistedSet(TEST_FILE);
		TEST_DATA.forEach(set::add);
		set.reset();
		assertEquals(0, set.size());
		TEST_DATA.forEach(data -> assertFalse(set.contains(data)));
	}
}
