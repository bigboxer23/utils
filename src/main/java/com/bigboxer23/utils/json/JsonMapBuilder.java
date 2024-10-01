package com.bigboxer23.utils.json;

import com.bigboxer23.utils.http.OkHttpUtil;
import com.squareup.moshi.Types;
import java.util.HashMap;
import java.util.Map;

/** utility class to quickly append values together and spit out a JSON map */
public class JsonMapBuilder {
	private final Map<String, Object> map;

	public JsonMapBuilder() {
		map = new HashMap<>();
	}

	public JsonMapBuilder put(String key, Object value) {
		map.put(key, value);
		return this;
	}

	public String toJson() {
		return OkHttpUtil.getMoshi()
				.adapter(Types.newParameterizedType(Map.class, String.class, Object.class))
				.toJson(map);
	}
}
