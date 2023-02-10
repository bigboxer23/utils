package com.bigboxer23.utils.http;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import okhttp3.*;

/** */
public class OkHttpUtil {
	private static final OkHttpClient client = new OkHttpClient.Builder()
			.connectTimeout(1, TimeUnit.MINUTES)
			.writeTimeout(1, TimeUnit.MINUTES)
			.readTimeout(1, TimeUnit.MINUTES)
			.callTimeout(1, TimeUnit.MINUTES)
			.build();

	public static void get(String url, Callback callback) {
		getCall(url, null).enqueue(callback);
	}

	public static void get(String url, Callback callback, RequestBuilderCallback builderCallback) {
		getCall(url, builderCallback).enqueue(callback);
	}

	public static Response getSynchronous(String url, RequestBuilderCallback builderCallback) throws IOException {
		return getCall(url, builderCallback).execute();
	}

	private static Call getCall(String url, RequestBuilderCallback builderCallback) {
		return client.newCall(runBuilderCallback(new Request.Builder().url(url).get(), builderCallback)
				.build());
	}

	public static void post(String url, Callback callback) {
		postCall(url, null, null).enqueue(callback);
	}

	public static void post(String url, Callback callback, RequestBuilderCallback builderCallback) {
		postCall(url, null, builderCallback).enqueue(callback);
	}

	public static Response postSynchronous(String url, RequestBody body, RequestBuilderCallback builderCallback)
			throws IOException {
		return postCall(url, body, builderCallback).execute();
	}

	private static Call postCall(String url, RequestBody body, RequestBuilderCallback builderCallback) {
		return client.newCall(runBuilderCallback(
						new Request.Builder().url(url).post(body == null ? RequestBody.create(new byte[0]) : body),
						builderCallback)
				.build());
	}

	private static Request.Builder runBuilderCallback(Request.Builder builder, RequestBuilderCallback builderCallback) {
		return Optional.ofNullable(builderCallback).orElse(builder1 -> builder1).modifyBuilder(builder);
	}
}
