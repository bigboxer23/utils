package com.bigboxer23.utils.logging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestWrappingCloseable {

	@Test
	void testConstructorWithVarArgs() throws IOException {
		Closeable c1 = mock(Closeable.class);
		Closeable c2 = mock(Closeable.class);

		WrappingCloseable wrapper = new WrappingCloseable(c1, c2);
		wrapper.close();

		verify(c1).close();
		verify(c2).close();
	}

	@Test
	void testConstructorWithCollection() throws IOException {
		Closeable c1 = mock(Closeable.class);
		Closeable c2 = mock(Closeable.class);
		List<Closeable> closeables = Arrays.asList(c1, c2);

		WrappingCloseable wrapper = new WrappingCloseable(closeables);
		wrapper.close();

		verify(c1).close();
		verify(c2).close();
	}

	@Test
	void testConstructorWithNullVarArgs() {
		WrappingCloseable wrapper = new WrappingCloseable((Closeable[]) null);
		assertDoesNotThrow(() -> wrapper.close());
	}

	@Test
	void testConstructorWithNullCollection() {
		WrappingCloseable wrapper = new WrappingCloseable((List<Closeable>) null);
		assertDoesNotThrow(() -> wrapper.close());
	}

	@Test
	void testCloseWithIOException() throws IOException {
		Closeable c1 = mock(Closeable.class);
		Closeable c2 = mock(Closeable.class);
		Closeable c3 = mock(Closeable.class);

		doThrow(new IOException("Test exception")).when(c2).close();

		WrappingCloseable wrapper = new WrappingCloseable(c1, c2, c3);
		assertDoesNotThrow(() -> wrapper.close());

		verify(c1).close();
		verify(c2).close();
		verify(c3).close();
	}

	@Test
	void testCloseWithEmptyList() {
		WrappingCloseable wrapper = new WrappingCloseable();
		assertDoesNotThrow(() -> wrapper.close());
	}

	@Test
	void testCloseMultipleTimes() throws IOException {
		Closeable c1 = mock(Closeable.class);

		WrappingCloseable wrapper = new WrappingCloseable(c1);
		wrapper.close();
		wrapper.close();

		verify(c1, times(2)).close();
	}

	@Test
	void testMixedVarArgsWithNulls() throws IOException {
		Closeable c1 = mock(Closeable.class);
		Closeable c2 = mock(Closeable.class);

		WrappingCloseable wrapper = new WrappingCloseable(c1, c2);
		wrapper.close();

		verify(c1).close();
		verify(c2).close();
	}

	@Test
	void testAllCloseablesThrowExceptions() throws IOException {
		Closeable c1 = mock(Closeable.class);
		Closeable c2 = mock(Closeable.class);

		doThrow(new IOException("Exception 1")).when(c1).close();
		doThrow(new IOException("Exception 2")).when(c2).close();

		WrappingCloseable wrapper = new WrappingCloseable(c1, c2);
		assertDoesNotThrow(() -> wrapper.close());

		verify(c1).close();
		verify(c2).close();
	}
}
