package com.bigboxer23.utils.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** */
public class TestFilePersistedIndex {
	@Test
	void testFilePersistentIndexIncrement() {
		FilePersistentIndex index = new FilePersistentIndex("testIndex");
		index.reset();
		int value = index.increment();
		assertEquals(0, value);
		assertEquals(1, index.increment());
	}

	@Test
	void testFilePersistentIndexSetAndGet() {
		FilePersistentIndex index = new FilePersistentIndex("testIndex");
		index.set(42);
		FilePersistentIndex newInstance = new FilePersistentIndex("testIndex");
		assertEquals(42, newInstance.get());
	}
}
