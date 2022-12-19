package com.bigboxer23.utils.http;

import okhttp3.*;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class OkHttpUtil
{
	private static final OkHttpClient client = new OkHttpClient.Builder()
			.connectTimeout(1, TimeUnit.MINUTES)
			.writeTimeout(1, TimeUnit.MINUTES)
			.readTimeout(1, TimeUnit.MINUTES)
			.callTimeout(1, TimeUnit.MINUTES)
			.build();

	public static void get(String url, Callback callback)
	{
		client.newCall(new Request.Builder()
				.url(url)
				.get()
				.build()).enqueue(callback);
	}


	public static void post(String url, Callback callback)
	{
		client.newCall(new Request.Builder()
				.url(url)
				.post(RequestBody.create(new byte[0]))
				.build()).enqueue(callback);
	}

}
