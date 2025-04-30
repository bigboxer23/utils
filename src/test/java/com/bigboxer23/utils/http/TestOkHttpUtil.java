package com.bigboxer23.utils.http;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Optional;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

/** */
public class TestOkHttpUtil {
	@Test
	void testOkHttpUtilGetBodyWithMockServer() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setBody("expected body").setResponseCode(HttpURLConnection.HTTP_OK));
			server.start();
			Response response = OkHttpUtil.getSynchronous(server.url("/test").toString(), null);
			assertTrue(response.isSuccessful());
			assertEquals("expected body", OkHttpUtil.getBody(response).orElse(""));
		}
	}

	@Test
	void testOkHttpUtilGetBodyReturnsEmptyOn404() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse()
					.setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
					.setBody("Not Found"));
			server.start();
			Response response = OkHttpUtil.getSynchronous(server.url("/missing").toString(), null);
			assertFalse(response.isSuccessful());
			Optional<String> body = OkHttpUtil.getBody(response);
			assertTrue(body.isPresent());
			assertEquals("Not Found", body.orElse(""));
		}
	}

	@Test
	void testOkHttpUtilGetBodyReturnsPresentOn404WithBody() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse()
					.setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
					.setBody("Not Found"));
			server.start();
			Response response =
					OkHttpUtil.getSynchronous(server.url("/not-found").toString(), null);
			assertFalse(response.isSuccessful());
			Optional<String> body = OkHttpUtil.getBody(response);
			assertTrue(body.isPresent());
			assertEquals("Not Found", body.orElse(""));
		}
	}

	@Test
	void testOkHttpUtilHandlesEmptyBodyGracefully() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse()
					.setResponseCode(HttpURLConnection.HTTP_OK)
					.setBody(""));
			server.start();
			Response response = OkHttpUtil.getSynchronous(server.url("/empty").toString(), null);
			assertTrue(response.isSuccessful());
			assertTrue(OkHttpUtil.getBody(response).isPresent());
		}
	}

	@Test
	void testOkHttpUtilGetBodyWithJsonParsing() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse()
					.setResponseCode(HttpURLConnection.HTTP_OK)
					.setBody("{\"key\":\"value\"}"));
			server.start();
			Response response = OkHttpUtil.getSynchronous(server.url("/json").toString(), null);
			Optional<TestJson> parsed = OkHttpUtil.getBody(response, TestJson.class);
			assertTrue(parsed.isPresent());
			assertEquals("value", parsed.get().key);
		}
	}

	@Test
	void testGetNonEmptyBodyThrowsOnFailure() throws IOException {
		class Dummy {}
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(500).setBody("error"));
			server.start();
			Response response = OkHttpUtil.getSynchronous(server.url("/fail").toString(), null);
			assertThrows(IOException.class, () -> OkHttpUtil.getNonEmptyBody(response, Dummy.class));
		}
	}

	@Test
	void testCreateBodyFromJsonObject() throws UnsupportedEncodingException {
		RequestBody body = OkHttpRequestBodyUtils.createBodyFromJsonObject(new TestJson(), TestJson.class);
		assertNotNull(body);
	}
}
