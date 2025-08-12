package com.bigboxer23.utils.logging;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

public class TestLoggingContextBuilder {

	@AfterEach
	void clearMDC() {
		MDC.clear();
	}

	@Test
	void testCreate() {
		LoggingContextBuilder builder = LoggingContextBuilder.create();
		assertNotNull(builder);
	}

	@Test
	void testAddDeviceId() {
		try (WrappingCloseable closeable =
				LoggingContextBuilder.create().addDeviceId("device123").build()) {
			assertEquals("device123", MDC.get("deviceId"));
		}
		assertNull(MDC.get("deviceId"));
	}

	@Test
	void testAddTemperature() {
		try (WrappingCloseable closeable =
				LoggingContextBuilder.create().addTemperature(25.5f).build()) {
			assertEquals("25.5", MDC.get("temperature"));
		}
		assertNull(MDC.get("temperature"));
	}

	@Test
	void testAddWatts() {
		try (WrappingCloseable closeable =
				LoggingContextBuilder.create().addWatts(100.0f).build()) {
			assertEquals("100.0", MDC.get("watts"));
		}
		assertNull(MDC.get("watts"));
	}

	@Test
	void testAddHumidity() {
		try (WrappingCloseable closeable =
				LoggingContextBuilder.create().addHumidity(70).build()) {
			assertEquals("70", MDC.get("humidity"));
		}
		assertNull(MDC.get("humidity"));
	}

	@Test
	void testAddCO2() {
		try (WrappingCloseable closeable =
				LoggingContextBuilder.create().addCO2(450).build()) {
			assertEquals("450", MDC.get("co2"));
		}
		assertNull(MDC.get("co2"));
	}

	@Test
	void testAddCommand() {
		try (WrappingCloseable closeable =
				LoggingContextBuilder.create().addCommand("test-command").build()) {
			assertEquals("test-command", MDC.get("command"));
		}
		assertNull(MDC.get("command"));
	}

	@Test
	void testAddMethod() {
		try (WrappingCloseable closeable =
				LoggingContextBuilder.create().addMethod("test-method").build()) {
			assertEquals("test-method", MDC.get("method"));
		}
		assertNull(MDC.get("method"));
	}

	@Test
	void testAddTraceId() {
		try (WrappingCloseable closeable =
				LoggingContextBuilder.create().addTraceId().build()) {
			String traceId = MDC.get("traceId");
			assertNotNull(traceId);
			assertTrue(traceId.length() > 0);
		}
		assertNull(MDC.get("traceId"));
	}

	@Test
	void testChainedCalls() {
		try (WrappingCloseable closeable = LoggingContextBuilder.create()
				.addDeviceId("device456")
				.addTemperature(22.1f)
				.addHumidity(60)
				.addCommand("chain-test")
				.build()) {
			assertEquals("device456", MDC.get("deviceId"));
			assertEquals("22.1", MDC.get("temperature"));
			assertEquals("60", MDC.get("humidity"));
			assertEquals("chain-test", MDC.get("command"));
		}
		assertNull(MDC.get("deviceId"));
		assertNull(MDC.get("temperature"));
		assertNull(MDC.get("humidity"));
		assertNull(MDC.get("command"));
	}

	@Test
	void testEmptyBuilder() {
		try (WrappingCloseable closeable = LoggingContextBuilder.create().build()) {}
	}

	@Test
	void testBuilderReturnsCorrectType() {
		LoggingContextBuilder builder = LoggingContextBuilder.create();
		assertSame(builder, builder.addDeviceId("test"));
		assertSame(builder, builder.addTemperature(1.0f));
		assertSame(builder, builder.addWatts(2.0f));
		assertSame(builder, builder.addHumidity(3));
		assertSame(builder, builder.addCO2(4));
		assertSame(builder, builder.addCommand("test"));
		assertSame(builder, builder.addMethod("test"));
		assertSame(builder, builder.addTraceId());
	}
}
