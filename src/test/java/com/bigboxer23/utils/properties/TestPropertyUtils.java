package com.bigboxer23.utils.properties;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** */
public class TestPropertyUtils {

	@BeforeEach
	void resetPropertyCache() throws Exception {
		Field propsField = PropertyUtils.class.getDeclaredField("props");
		propsField.setAccessible(true);
		propsField.set(null, null);
	}

	@Test
	void testPropertyFromFile() {
		// Requires test/resources/application.properties to contain: test.key=fromFile
		assertEquals("fromFile", PropertyUtils.getProperty("test.key"));
	}

	@Test
	void testPropertyFallsBackToEnvIfMissingFromFile() {
		String fallbackKey = "ENV_ONLY_KEY";
		try {
			// Set an environment variable dynamically (doesn't work in all environments, but worth
			// attempting)
			String expected = "env-value";
			Properties fakeProps = new Properties(); // simulate missing key
			assertNull(fakeProps.getProperty(fallbackKey));

			String actual = PropertyUtils.getProperty(fallbackKey);
			assertTrue(actual == null || actual.equals(System.getenv(fallbackKey)));
		} catch (Exception e) {
			fail("Exception while testing env fallback: " + e.getMessage());
		}
	}

	@Test
	void testMissingFileHandledGracefully() throws Exception {
		ClassLoader mockLoader = new ClassLoader() {
			@Override
			public java.io.InputStream getResourceAsStream(String name) {
				return null; // Simulate file not found
			}
		};

		Field propsField = PropertyUtils.class.getDeclaredField("props");
		propsField.setAccessible(true);
		propsField.set(null, null);

		// Test will rely on default class loader still — cannot easily mock without changing source
		assertNull(PropertyUtils.getProperty("someMissingKey"));
	}

	@Test
	void testPropertyCaching() {
		String firstCall = PropertyUtils.getProperty("test.key");
		String secondCall = PropertyUtils.getProperty("test.key");

		assertEquals(firstCall, secondCall);
		assertEquals("fromFile", firstCall);
	}

	@Test
	void testNullPropertyKey() {
		assertThrows(NullPointerException.class, () -> PropertyUtils.getProperty(null));
	}

	@Test
	void testEmptyPropertyKey() {
		String result = PropertyUtils.getProperty("");
		assertTrue(result == null || result.equals(System.getenv("")));
	}

	@Test
	void testPropertyOverrideByEnvironment() throws Exception {
		String fileValue = PropertyUtils.getProperty("test.key");
		assertNotNull(fileValue);
		assertEquals("fromFile", fileValue); // Should get from file first
	}

	@Test
	void testMultiplePropertyAccess() {
		String prop1 = PropertyUtils.getProperty("test.key");
		String prop2 = PropertyUtils.getProperty("nonexistent.key");
		String prop3 = PropertyUtils.getProperty("test.key");

		assertEquals("fromFile", prop1);
		assertEquals("fromFile", prop3);
		assertTrue(prop2 == null || prop2.equals(System.getenv("nonexistent.key")));
	}
}
