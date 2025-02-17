package com.bigboxer23.utils.logging;

import org.slf4j.MDC;

/** */
public class LoggingUtil {
	public static void addContext(String method, String deviceId) {
		addMethod(method);
		addDeviceId(deviceId);
	}

	public static void addDeviceId(String deviceId) {
		MDC.put("deviceId", deviceId);
	}

	public static void clearDeviceId() {
		MDC.remove("deviceId");
	}

	public static void addMethod(String deviceId) {
		MDC.put("method", deviceId);
	}

	public static void clearContext() {
		MDC.clear();
	}
}
