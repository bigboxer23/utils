package com.bigboxer23.utils.http;

import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public class OkHttpUtil {
	private static final Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);

	private static final OkHttpClient defaultClient = getBuilder().build();

	private static Moshi moshi;

	public static OkHttpClient.Builder getBuilder() {
		return new OkHttpClient.Builder()
				.connectTimeout(3, TimeUnit.MINUTES)
				.writeTimeout(3, TimeUnit.MINUTES)
				.readTimeout(3, TimeUnit.MINUTES)
				.callTimeout(3, TimeUnit.MINUTES);
	}

	public static void get(String url, Callback callback) {
		get(url, callback, null, defaultClient);
	}

	public static void get(String url, Callback callback, OkHttpClient client) {
		getCall(url, null, client).enqueue(callback);
	}

	public static void get(String url, Callback callback, RequestBuilderCallback builderCallback) {
		get(url, callback, builderCallback, defaultClient);
	}

	public static void get(String url, Callback callback, RequestBuilderCallback builderCallback, OkHttpClient client) {
		getCall(url, builderCallback, client).enqueue(callback);
	}

	public static Response getSynchronous(String url, RequestBuilderCallback builderCallback) throws IOException {
		return getSynchronous(url, builderCallback, defaultClient);
	}

	public static Response getSynchronous(String url, RequestBuilderCallback builderCallback, OkHttpClient client)
			throws IOException {
		return getCall(url, builderCallback, client).execute();
	}

	private static Call getCall(String url, RequestBuilderCallback builderCallback, OkHttpClient client) {
		return client.newCall(runBuilderCallback(new Request.Builder().url(url).get(), builderCallback)
				.build());
	}

	public static void post(String url, Callback callback) {
		post(url, callback, null, defaultClient);
	}

	public static void post(String url, Callback callback, RequestBuilderCallback builderCallback) {
		post(url, callback, builderCallback, defaultClient);
	}

	public static void post(
			String url, Callback callback, RequestBuilderCallback builderCallback, OkHttpClient client) {
		postCall(url, null, builderCallback, client).enqueue(callback);
	}

	public static Response postSynchronous(String url, RequestBody body, RequestBuilderCallback builderCallback)
			throws IOException {
		return postSynchronous(url, body, builderCallback, defaultClient);
	}

	public static Response postSynchronous(
			String url, RequestBody body, RequestBuilderCallback builderCallback, OkHttpClient client)
			throws IOException {
		return postCall(url, body, builderCallback, client).execute();
	}

	private static Call postCall(
			String url, RequestBody body, RequestBuilderCallback builderCallback, OkHttpClient client) {
		return client.newCall(runBuilderCallback(
						new Request.Builder().url(url).post(body == null ? RequestBody.create(new byte[0]) : body),
						builderCallback)
				.build());
	}

	public static void delete(String url, Callback callback, RequestBuilderCallback builderCallback)
			throws IOException {
		delete(url, callback, builderCallback, defaultClient);
	}

	public static void delete(
			String url, Callback callback, RequestBuilderCallback builderCallback, OkHttpClient client)
			throws IOException {
		deleteCall(url, builderCallback, client).enqueue(callback);
	}

	public static Response deleteSynchronous(String url, RequestBuilderCallback builderCallback) throws IOException {
		return deleteSynchronous(url, builderCallback, defaultClient);
	}

	public static Response deleteSynchronous(String url, RequestBuilderCallback builderCallback, OkHttpClient client)
			throws IOException {
		return deleteCall(url, builderCallback, client).execute();
	}

	private static Call deleteCall(String url, RequestBuilderCallback builderCallback, OkHttpClient client) {
		return client.newCall(runBuilderCallback(new Request.Builder().url(url).delete(), builderCallback)
				.build());
	}

	public static void put(String url, Callback callback, RequestBuilderCallback builderCallback) {
		put(url, callback, builderCallback, defaultClient);
	}

	public static void put(String url, Callback callback, RequestBuilderCallback builderCallback, OkHttpClient client) {
		putCall(url, null, builderCallback, client).enqueue(callback);
	}

	public static Response putSynchronous(String url, RequestBody body, RequestBuilderCallback builderCallback)
			throws IOException {
		return putSynchronous(url, body, builderCallback, defaultClient);
	}

	public static Response putSynchronous(
			String url, RequestBody body, RequestBuilderCallback builderCallback, OkHttpClient client)
			throws IOException {
		return putCall(url, body, builderCallback, client).execute();
	}

	private static Call putCall(
			String url, RequestBody body, RequestBuilderCallback builderCallback, OkHttpClient client) {
		return client.newCall(runBuilderCallback(
						new Request.Builder().url(url).put(body == null ? RequestBody.create(new byte[0]) : body),
						builderCallback)
				.build());
	}

	private static Request.Builder runBuilderCallback(Request.Builder builder, RequestBuilderCallback builderCallback) {
		return Optional.ofNullable(builderCallback).orElse(builder1 -> builder1).modifyBuilder(builder);
	}

	public static Optional<String> getBody(Response response) {
		ResponseBody body = response.body();
		if (!response.isSuccessful()) {
			logger.error("request not successful: " + response.code());
		}
		if (body == null) {
			return Optional.empty();
		}
		try {
			return Optional.of(body.string());
		} catch (IOException theE) {
			return Optional.empty();
		}
	}

	public static <T> Optional<T> getBody(Response response, Class<T> clazz) {
		Optional<String> body = getBody(response);
		if (!body.isPresent()) {
			logger.error("empty body");
			return Optional.empty();
		}
		if (!response.isSuccessful()) {
			logger.error("request not successful body: " + body.get());
			return Optional.empty();
		}
		try {
			return Optional.ofNullable(getMoshi().adapter(clazz).fromJson(body.get()));
		} catch (IOException e) {
			logger.error("getBody: ", e);
			return Optional.empty();
		}
	}

	public static <T> T getNonEmptyBody(Response response, Class<T> clazz) throws IOException {
		Optional<T> responseBody = getBody(response, clazz);
		if (!responseBody.isPresent()) {
			throw new IOException("getBodyChecked: " + response.code() + " " + response.message());
		}
		return responseBody.get();
	}

	private static Moshi getMoshi() {
		if (moshi == null) {
			moshi = new Moshi.Builder().build();
		}
		return moshi;
	}
}
