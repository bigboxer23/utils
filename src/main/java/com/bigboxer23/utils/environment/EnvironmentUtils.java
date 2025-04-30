package com.bigboxer23.utils.environment;

/** */
public class EnvironmentUtils {
	public static float celciusToFahrenheit(float celcius) {
		return (celcius * 1.8f) + 32f;
	}

	public static double fahrenheitToCelsius(double fahrenheit) {
		return (fahrenheit - 32f) * (5f / 9f);
	}
}
