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

			CountDownLatch latch = new CountDownLatch(1);

			Request request1 = new Request.Builder().url(server.url("/fail")).build();
			client.newCall(request1).enqueue(new OkHttpCallback() {
				@Override
				public void onFailure(Call call, IOException e) {
					super.onFailure(call, e);
					latch.countDown();
				}
			});

			Request request2 = new Request.Builder().url(server.url("/success")).build();
			client.newCall(request2).enqueue(new OkHttpCallback() {
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					super.onResponse(call, response);
					assertTrue(response.isSuccessful());
					latch.countDown();
				}
			});

			assertTrue(latch.await(5, TimeUnit.SECONDS));
		}
	}
}
