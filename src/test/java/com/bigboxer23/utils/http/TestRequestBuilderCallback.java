package com.bigboxer23.utils.http;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import okhttp3.Request;
import org.junit.jupiter.api.Test;

public class TestRequestBuilderCallback {

	@Test
	void testModifyBuilder() {
		RequestBuilderCallback callback = builder -> builder.addHeader("X-Custom-Header", "test-value");

		Request.Builder originalBuilder = new Request.Builder().url("http://example.com");
		Request.Builder modifiedBuilder = callback.modifyBuilder(originalBuilder);

		assertSame(originalBuilder, modifiedBuilder);

		Request request = modifiedBuilder.build();
		assertEquals("test-value", request.header("X-Custom-Header"));
	}

	@Test
	void testModifyBuilderWithMultipleHeaders() {
		RequestBuilderCallback callback = builder -> builder.addHeader("Authorization", "Bearer token123")
				.addHeader("Content-Type", "application/json");

		Request.Builder builder = new Request.Builder().url("http://example.com");
		Request request = callback.modifyBuilder(builder).build();

		assertEquals("Bearer token123", request.header("Authorization"));
		assertEquals("application/json", request.header("Content-Type"));
	}

	@Test
	void testModifyBuilderReturnsNull() {
		RequestBuilderCallback callback = builder -> null;

		Request.Builder builder = new Request.Builder().url("http://example.com");
		Request.Builder result = callback.modifyBuilder(builder);

		assertNull(result);
	}

	@Test
	void testModifyBuilderWithMethod() {
		RequestBuilderCallback callback = builder -> builder.post(okhttp3.RequestBody.create("test body", okhttp3.MediaType.parse("text/plain")));

		Request.Builder builder = new Request.Builder().url("http://example.com");
		Request request = callback.modifyBuilder(builder).build();

		assertEquals("POST", request.method());
		assertNotNull(request.body());
	}

	@Test
	void testModifyBuilderNoChanges() {
		RequestBuilderCallback callback = builder -> builder;

		Request.Builder originalBuilder = new Request.Builder().url("http://example.com");
		Request.Builder modifiedBuilder = callback.modifyBuilder(originalBuilder);

		assertSame(originalBuilder, modifiedBuilder);

		Request request = modifiedBuilder.build();
		assertEquals("GET", request.method());
		assertEquals("http://example.com/", request.url().toString());
	}

	@Test
	void testModifyBuilderWithMock() {
		RequestBuilderCallback mockCallback = mock(RequestBuilderCallback.class);
		Request.Builder builder = new Request.Builder().url("http://example.com");
		Request.Builder expectedBuilder = new Request.Builder().url("http://modified.com");

		when(mockCallback.modifyBuilder(builder)).thenReturn(expectedBuilder);

		Request.Builder result = mockCallback.modifyBuilder(builder);

		assertSame(expectedBuilder, result);
		verify(mockCallback).modifyBuilder(builder);
	}
}
