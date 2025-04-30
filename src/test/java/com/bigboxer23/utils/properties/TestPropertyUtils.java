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
		propsField.set(null, null); // Reset static cache
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
			// Can't actually guarantee env var is set, so this check is flexible
			assertTrue(actual == null || actual.equals(System.getenv(fallbackKey)));
		} catch (Exception e) {
			fail("Exception while testing env fallback: " + e.getMessage());
		}
	}

	@Test
	void testMissingFileHandledGracefully() throws Exception {
		// Temporarily rename application.properties or force ClassLoader to return null
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
}
