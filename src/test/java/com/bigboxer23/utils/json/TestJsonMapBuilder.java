package com.bigboxer23.utils.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** */
public class TestJsonMapBuilder {
	@Test
	void testJsonMapBuilder() {
		JsonMapBuilder builder = new JsonMapBuilder().put("key", "value");
		assertEquals("{\"key\":\"value\"}", builder.toJson());
	}
}
