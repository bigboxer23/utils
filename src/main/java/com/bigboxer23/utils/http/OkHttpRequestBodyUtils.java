package com.bigboxer23.utils.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import okhttp3.RequestBody;

/** */
public class OkHttpRequestBodyUtils {
	public static RequestBody createEmptyBody() {
		return RequestBody.create(new byte[0]);
	}

	public static RequestBody createBodyFromString(String body) throws UnsupportedEncodingException {
		return RequestBody.create(
				URLDecoder.decode(body, StandardCharsets.UTF_8.displayName()).getBytes(StandardCharsets.UTF_8));
	}

	public static <D> RequestBody createBodyFromJsonObject(Object object, Class<D> clazz)
			throws UnsupportedEncodingException {
		if (!clazz.isInstance(object)) {
			throw new IllegalArgumentException("Object is not an instance of " + clazz.getName());
		}
		return RequestBody.create(URLDecoder.decode(
						OkHttpUtil.getMoshi().adapter(clazz).toJson((D) object), StandardCharsets.UTF_8.displayName())
				.getBytes(StandardCharsets.UTF_8));
	}
}
