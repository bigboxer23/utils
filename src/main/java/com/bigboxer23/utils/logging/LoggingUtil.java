package com.bigboxer23.utils.logging;

import java.io.Closeable;
import org.slf4j.MDC;

/** */
public class LoggingUtil {
	public static Closeable addDeviceId(String deviceId) {
		return MDC.putCloseable("deviceId", deviceId);
	}

	public static void clearDeviceId() {
		MDC.remove("deviceId");
	}

	public static Closeable addTemperature(float temperature) {
		return MDC.putCloseable("temperature", "" + temperature);
	}

	public static Closeable addWatts(float watts) {
		return MDC.putCloseable("watts", "" + watts);
	}

	public static Closeable addHumidity(int humidity) {
		return MDC.putCloseable("humidity", "" + humidity);
	}

	public static Closeable addCO2(int co2) {
		return MDC.putCloseable("co2", "" + co2);
	}

	public static Closeable addCommand(String command) {
		return MDC.putCloseable("command", command);
	}

	public static Closeable addMethod(String method) {
		return MDC.putCloseable("method", method);
	}

	public static void clearContext() {
		MDC.clear();
	}
}
