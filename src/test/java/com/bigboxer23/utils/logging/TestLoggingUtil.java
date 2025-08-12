package com.bigboxer23.utils.logging;

import static org.junit.jupiter.api.Assertions.*;

import com.bigboxer23.utils.command.Command;
import com.bigboxer23.utils.command.VoidCommand;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

public class TestLoggingUtil {

	@AfterEach
	void clearMDC() {
		MDC.clear();
	}

	@Test
	void testAddDeviceId() {
		try (MDC.MDCCloseable closeable = LoggingUtil.addDeviceId("device123")) {
			assertEquals("device123", MDC.get("deviceId"));
		}
		assertNull(MDC.get("deviceId"));
	}

	@Test
	void testClearDeviceId() {
		MDC.put("deviceId", "test");
		LoggingUtil.clearDeviceId();
		assertNull(MDC.get("deviceId"));
	}

	@Test
	void testAddTemperature() {
		try (MDC.MDCCloseable closeable = LoggingUtil.addTemperature(23.5f)) {
			assertEquals("23.5", MDC.get("temperature"));
		}
		assertNull(MDC.get("temperature"));
	}

	@Test
	void testAddWatts() {
		try (MDC.MDCCloseable closeable = LoggingUtil.addWatts(150.0f)) {
			assertEquals("150.0", MDC.get("watts"));
		}
		assertNull(MDC.get("watts"));
	}

	@Test
	void testAddHumidity() {
		try (MDC.MDCCloseable closeable = LoggingUtil.addHumidity(65)) {
			assertEquals("65", MDC.get("humidity"));
		}
		assertNull(MDC.get("humidity"));
	}

	@Test
	void testAddCO2() {
		try (MDC.MDCCloseable closeable = LoggingUtil.addCO2(400)) {
			assertEquals("400", MDC.get("co2"));
		}
		assertNull(MDC.get("co2"));
	}

	@Test
	void testAddCommand() {
		try (MDC.MDCCloseable closeable = LoggingUtil.addCommand("testCommand")) {
			assertEquals("testCommand", MDC.get("command"));
		}
		assertNull(MDC.get("command"));
	}

	@Test
	void testAddMethod() {
		try (MDC.MDCCloseable closeable = LoggingUtil.addMethod("testMethod")) {
			assertEquals("testMethod", MDC.get("method"));
		}
		assertNull(MDC.get("method"));
	}

	@Test
	void testAddTraceId() {
		try (MDC.MDCCloseable closeable = LoggingUtil.addTraceId()) {
			String traceId = MDC.get("traceId");
			assertNotNull(traceId);
			assertTrue(traceId.length() > 0);
		}
		assertNull(MDC.get("traceId"));
	}

	@Test
	void testClearContext() {
		MDC.put("test1", "value1");
		MDC.put("test2", "value2");
		LoggingUtil.clearContext();
		assertNull(MDC.get("test1"));
		assertNull(MDC.get("test2"));
	}

	@Test
	void testRunTracedCommand() throws IOException, InterruptedException {
		final boolean[] executed = {false};
		Command<String> command = () -> {
			executed[0] = true;
			assertNotNull(MDC.get("traceId"));
			return "result";
		};

		LoggingUtil.runTraced(command);
		assertTrue(executed[0]);
		assertNull(MDC.get("traceId"));
	}

	@Test
	void testRunTracedVoidCommand() throws IOException, InterruptedException {
		final boolean[] executed = {false};
		VoidCommand command = () -> {
			executed[0] = true;
			assertNotNull(MDC.get("traceId"));
		};

		LoggingUtil.runTraced(command);
		assertTrue(executed[0]);
		assertNull(MDC.get("traceId"));
	}

	@Test
	void testRunTracedCommandThrowsException() {
		Command<String> command = () -> {
			throw new IOException("Test exception");
		};

		assertThrows(IOException.class, () -> LoggingUtil.runTraced(command));
		assertNull(MDC.get("traceId"));
	}

	@Test
	void testRunTracedVoidCommandThrowsException() {
		VoidCommand command = () -> {
			throw new InterruptedException("Test exception");
		};

		assertThrows(InterruptedException.class, () -> LoggingUtil.runTraced(command));
		assertNull(MDC.get("traceId"));
	}
}
