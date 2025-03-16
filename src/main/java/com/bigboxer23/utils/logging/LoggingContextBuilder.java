package com.bigboxer23.utils.logging;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/** */
public class LoggingContextBuilder {
	List<Closeable> closeables = new ArrayList<>();

	public LoggingContextBuilder addDeviceId(String deviceId) {
		closeables.add(LoggingUtil.addDeviceId(deviceId));
		return this;
	}

	public LoggingContextBuilder addTemperature(float temperature) {
		closeables.add(LoggingUtil.addTemperature(temperature));
		return this;
	}

	public LoggingContextBuilder addWatts(float watts) {
		closeables.add(LoggingUtil.addWatts(watts));
		return this;
	}

	public LoggingContextBuilder addHumidity(int humidity) {
		closeables.add(LoggingUtil.addHumidity(humidity));
		return this;
	}

	public LoggingContextBuilder addCO2(int co2) {
		closeables.add(LoggingUtil.addCO2(co2));
		return this;
	}

	public LoggingContextBuilder addCommand(String command) {
		closeables.add(LoggingUtil.addCommand(command));
		return this;
	}

	public LoggingContextBuilder addMethod(String method) {
		closeables.add(LoggingUtil.addMethod(method));
		return this;
	}

	public LoggingContextBuilder addTraceId() {
		closeables.add(LoggingUtil.addTraceId());
		return this;
	}

	public WrappingCloseable build() {
		return new WrappingCloseable(closeables);
	}

	public static LoggingContextBuilder create() {
		return new LoggingContextBuilder();
	}
}
