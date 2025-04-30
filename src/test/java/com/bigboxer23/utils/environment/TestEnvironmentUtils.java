package com.bigboxer23.utils.environment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** */
public class TestEnvironmentUtils {
	@Test
	void testCelsiusToFahrenheit() {
		assertEquals(32f, EnvironmentUtils.celciusToFahrenheit(0), 0.001);
		assertEquals(212f, EnvironmentUtils.celciusToFahrenheit(100), 0.001);
	}

	@Test
	void testFahrenheitToCelsius() {
		assertEquals(0, EnvironmentUtils.fahrenheitToCelsius(32), 0.001);
		assertEquals(100, EnvironmentUtils.fahrenheitToCelsius(212), 0.001);
	}
}
