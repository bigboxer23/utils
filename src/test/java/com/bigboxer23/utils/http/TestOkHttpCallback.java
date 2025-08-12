package com.bigboxer23.utils.http;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import okhttp3.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

public class TestOkHttpCallback {
	@Test
	void testOkHttpCallbackOnFailureAndSuccess() throws Exception {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(500).setBody("Server Error"));
			server.enqueue(new MockResponse().setResponseCode(200).setBody("Success"));
			server.start();
			OkHttpClient client = new OkHttpClient();

			CountDownLatch latch = new CountDownLatch(2); // Wait for both async requests

			Request request1 = new Request.Builder().url(server.url("/fail")).build();
			client.newCall(request1).enqueue(new OkHttpCallback() {
				@Override
				public void onFailure(Call call, IOException e) {
					try {
						super.onFailure(call, e);
					} finally {
						latch.countDown();
					}
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					try {
						super.onResponse(call, response);
						// Check that this is the failed response
						assertFalse(response.isSuccessful());
					} finally {
						latch.countDown();
					}
				}
			});

			Request request2 = new Request.Builder().url(server.url("/success")).build();
			client.newCall(request2).enqueue(new OkHttpCallback() {
				@Override
				public void onFailure(Call call, IOException e) {
					try {
						super.onFailure(call, e);
					} finally {
						latch.countDown();
					}
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					try {
						super.onResponse(call, response);
						// Check that this is the success response
						assertTrue(response.isSuccessful());
					} finally {
						latch.countDown();
					}
				}
			});

			assertTrue(latch.await(5, TimeUnit.SECONDS), "Callbacks did not complete in time");
		}
	}

	@Test
	void testOkHttpCallbackOnResponseBodyString() throws Exception {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(200).setBody("test response"));
			server.start();

			CountDownLatch latch = new CountDownLatch(1);
			String[] capturedBody = {null};

			OkHttpCallback callback = new OkHttpCallback() {
				@Override
				public void onResponseBodyString(Call call, String stringBody) {
					capturedBody[0] = stringBody;
					latch.countDown();
				}
			};

			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(server.url("/test")).build();
			client.newCall(request).enqueue(callback);

			assertTrue(latch.await(5, TimeUnit.SECONDS), "Callback did not complete in time");
			assertEquals("test response", capturedBody[0]);
		}
	}

	@Test
	void testOkHttpCallbackOnResponseBodyStringDoesNotThrow() throws Exception {
		OkHttpCallback callback = new OkHttpCallback();
		String testBody = "test body content";
		assertDoesNotThrow(() -> callback.onResponseBodyString(null, testBody));
	}

	@Test
	void testOkHttpCallbackOnFailureLogsWarning() {
		OkHttpCallback callback = new OkHttpCallback();
		IOException testException = new IOException("Test failure");

		Call mockCall = new Call() {
			@Override
			public Request request() {
				return new Request.Builder().url("http://test.example").build();
			}

			@Override
			public Response execute() throws IOException {
				return null;
			}

			@Override
			public void enqueue(Callback responseCallback) {}

			@Override
			public void cancel() {}

			@Override
			public boolean isExecuted() {
				return false;
			}

			@Override
			public boolean isCanceled() {
				return false;
			}

			@Override
			public Call clone() {
				return null;
			}

			@Override
			public okio.Timeout timeout() {
				return null;
			}
		};

		// This should not throw, just log the warning
		assertDoesNotThrow(() -> callback.onFailure(mockCall, testException));
	}
}
