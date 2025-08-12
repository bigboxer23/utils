package com.bigboxer23.utils.http;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
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

	@Test
	void testCreateBodyFromStringWithSpecialCharacters() throws Exception {
		String testString = "test=value%20with%20spaces";
		RequestBody body = OkHttpRequestBodyUtils.createBodyFromString(testString);
		assertNotNull(body);
		assertTrue(body.contentLength() > 0);
	}

	@Test
	void testCreateBodyFromStringWithEmptyString() throws Exception {
		RequestBody body = OkHttpRequestBodyUtils.createBodyFromString("");
		assertNotNull(body);
		assertEquals(0, body.contentLength());
	}

	@Test
	void testCreateBodyFromJsonObjectWithNullValues() throws Exception {
		TestObject obj = new TestObject();
		obj.value = null;
		RequestBody body = OkHttpRequestBodyUtils.createBodyFromJsonObject(obj, TestObject.class);
		assertNotNull(body);
	}

	@Test
	void testCreateBodyFromJsonObjectWithComplexObject() throws Exception {
		ComplexTestObject obj = new ComplexTestObject();
		obj.name = "test name";
		obj.count = 42;
		obj.active = true;
		RequestBody body = OkHttpRequestBodyUtils.createBodyFromJsonObject(obj, ComplexTestObject.class);
		assertNotNull(body);
		assertTrue(body.contentLength() > 0);
	}

	@Test
	void testCreateEmptyBodyHasZeroLength() throws IOException {
		RequestBody body = OkHttpRequestBodyUtils.createEmptyBody();
		assertEquals(0, body.contentLength());
	}

	@Test
	void testCreateBodyFromStringWithUnicodeCharacters() throws Exception {
		String unicodeString = "test=value%E2%9C%93"; // URL encoded checkmark
		RequestBody body = OkHttpRequestBodyUtils.createBodyFromString(unicodeString);
		assertNotNull(body);
		assertTrue(body.contentLength() > 0);
	}

	static class TestObject {
		public String value;
	}

	static class ComplexTestObject {
		public String name;
		public int count;
		public boolean active;
	}
}
