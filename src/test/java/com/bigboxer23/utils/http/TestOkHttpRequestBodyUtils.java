package com.bigboxer23.utils.http;

import static org.junit.jupiter.api.Assertions.*;

import okhttp3.RequestBody;
import org.junit.jupiter.api.Test;

public class TestOkHttpRequestBodyUtils {
	@Test
	void testCreateEmptyBody() {
		RequestBody body = OkHttpRequestBodyUtils.createEmptyBody();
		assertNotNull(body);
	}

	@Test
	void testCreateBodyFromString() throws Exception {
		RequestBody body = OkHttpRequestBodyUtils.createBodyFromString("test=value");
		assertNotNull(body);
	}

	@Test
	void testCreateBodyFromJsonObject() throws Exception {
		TestObject obj = new TestObject();
		obj.value = "test";
		RequestBody body = OkHttpRequestBodyUtils.createBodyFromJsonObject(obj, TestObject.class);
		assertNotNull(body);
	}

	@Test
	void testCreateBodyFromJsonObjectThrowsIfWrongType() {
		assertThrows(
				IllegalArgumentException.class,
				() -> OkHttpRequestBodyUtils.createBodyFromJsonObject("not a TestObject", TestObject.class));
	}

	static class TestObject {
		public String value;
	}
}
