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
}
