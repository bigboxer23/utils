package com.bigboxer23.utils.logging;

import com.bigboxer23.utils.command.Command;
import com.bigboxer23.utils.command.VoidCommand;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;

/** */
public class LoggingUtil {
	public static MDC.MDCCloseable addDeviceId(String deviceId) {
		return MDC.putCloseable("deviceId", deviceId);
	}

	public static void clearDeviceId() {
		MDC.remove("deviceId");
	}

	public static MDC.MDCCloseable addTemperature(float temperature) {
		return MDC.putCloseable("temperature", "" + temperature);
	}

	public static MDC.MDCCloseable addWatts(float watts) {
		return MDC.putCloseable("watts", "" + watts);
	}

	public static MDC.MDCCloseable addHumidity(int humidity) {
		return MDC.putCloseable("humidity", "" + humidity);
	}

	public static MDC.MDCCloseable addCO2(int co2) {
		return MDC.putCloseable("co2", "" + co2);
	}

	public static MDC.MDCCloseable addCommand(String command) {
		return MDC.putCloseable("command", command);
	}

	public static MDC.MDCCloseable addMethod(String method) {
		return MDC.putCloseable("method", method);
	}

	public static MDC.MDCCloseable addTraceId() {
		return MDC.putCloseable("traceId", UUID.randomUUID().toString());
	}

	public static void clearContext() {
		MDC.clear();
	}

	public static void runTraced(Command command) throws IOException, InterruptedException {
		try (WrappingCloseable c = LoggingContextBuilder.create().addTraceId().build()) {
			command.execute();
		}
	}

	public static void runTraced(VoidCommand command) throws IOException, InterruptedException {
		try (WrappingCloseable c = LoggingContextBuilder.create().addTraceId().build()) {
			command.execute();
		}
	}
}
