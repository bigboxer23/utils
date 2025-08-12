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
			assertFalse(OkHttpUtil.getBody(response).isPresent());
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

	@Test
	void testGetBodyWithNullResponseReturnsEmpty() {
		assertFalse(OkHttpUtil.getBody(null).isPresent());
	}

	@Test
	void testGetBodyWithInvalidJsonReturnsEmpty() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse()
					.setResponseCode(HttpURLConnection.HTTP_OK)
					.setBody("not-a-json"));
			server.start();
			Response response = OkHttpUtil.getSynchronous(server.url("/badjson").toString(), null);
			Optional<TestJson> parsed = OkHttpUtil.getBody(response, TestJson.class);
			assertFalse(parsed.isPresent());
		}
	}

	@Test
	void testGetNonEmptyBodyReturnsParsedJson() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse()
					.setResponseCode(HttpURLConnection.HTTP_OK)
					.setBody("{\"key\":\"value\"}"));
			server.start();
			Response response =
					OkHttpUtil.getSynchronous(server.url("/goodjson").toString(), null);
			TestJson result = OkHttpUtil.getNonEmptyBody(response, TestJson.class);
			assertNotNull(result);
			assertEquals("value", result.key);
		}
	}

	@Test
	void testCreateBodyFromJsonObjectHandlesNullInputThrowsException() {
		assertThrows(
				IllegalArgumentException.class,
				() -> OkHttpRequestBodyUtils.createBodyFromJsonObject(null, TestJson.class));
	}

	@Test
	void testGetSynchronousInvalidUrlThrows() {
		assertThrows(IllegalArgumentException.class, () -> OkHttpUtil.getSynchronous("invalid-url", null));
	}

	@Test
	void testGetBodyEmptyBodyReturnsEmptyOptional() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(200).setBody(""));
			server.start();
			Response response =
					OkHttpUtil.getSynchronous(server.url("/emptybody").toString(), null);
			Optional<String> body = OkHttpUtil.getBody(response);
			assertFalse(body.isPresent());
		}
	}

	@Test
	void testGetBuilderReturnsConfiguredBuilder() {
		assertNotNull(OkHttpUtil.getBuilder());
		assertNotNull(OkHttpUtil.getBuilder().build());
	}

	@Test
	void testGetMoshiReturnsSameInstance() {
		assertNotNull(OkHttpUtil.getMoshi());
		assertSame(OkHttpUtil.getMoshi(), OkHttpUtil.getMoshi());
	}

	@Test
	void testPostSynchronousWithNullBody() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(200).setBody("posted"));
			server.start();
			Response response = OkHttpUtil.postSynchronous(server.url("/post").toString(), null, null);
			assertTrue(response.isSuccessful());
			assertEquals("posted", OkHttpUtil.getBody(response).orElse(""));
		}
	}

	@Test
	void testPutSynchronousWithNullBody() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(200).setBody("put"));
			server.start();
			Response response = OkHttpUtil.putSynchronous(server.url("/put").toString(), null, null);
			assertTrue(response.isSuccessful());
			assertEquals("put", OkHttpUtil.getBody(response).orElse(""));
		}
	}

	@Test
	void testDeleteSynchronous() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(204));
			server.start();
			try (Response response =
					OkHttpUtil.deleteSynchronous(server.url("/delete").toString(), null)) {
				assertEquals(204, response.code());
			}
		}
	}

	@Test
	void testGetBodyWithClassOnEmptyResponseReturnsEmpty() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(200).setBody(""));
			server.start();
			Response response = OkHttpUtil.getSynchronous(server.url("/empty").toString(), null);
			Optional<TestJson> result = OkHttpUtil.getBody(response, TestJson.class);
			assertFalse(result.isPresent());
		}
	}

	@Test
	void testGetBodyWithClassOn404ReturnsEmpty() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(404).setBody("{\"key\":\"value\"}"));
			server.start();
			Response response =
					OkHttpUtil.getSynchronous(server.url("/notfound").toString(), null);
			Optional<TestJson> result = OkHttpUtil.getBody(response, TestJson.class);
			assertFalse(result.isPresent());
		}
	}

	@Test
	void testRunBuilderCallbackWithNullCallback() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(200).setBody("success"));
			server.start();
			Response response = OkHttpUtil.getSynchronous(server.url("/test").toString(), null);
			assertTrue(response.isSuccessful());
		}
	}

	@Test
	void testGetSynchronousWithCallback() throws IOException {
		try (MockWebServer server = new MockWebServer()) {
			server.enqueue(new MockResponse().setResponseCode(200).setBody("with-header"));
			server.start();
			Response response = OkHttpUtil.getSynchronous(
					server.url("/test").toString(), builder -> builder.addHeader("Test-Header", "test-value"));
			assertTrue(response.isSuccessful());
			assertEquals("with-header", OkHttpUtil.getBody(response).orElse(""));
		}
	}
}
