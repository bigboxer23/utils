package com.bigboxer23.utils.http;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public class OkHttpCallback implements Callback {
	private static final Logger logger = LoggerFactory.getLogger(OkHttpCallback.class);

	@Override
	public void onFailure(Call call, IOException e) {
		logger.warn("call to " + call.request().url().url() + " failed.", e);
	}

	@Override
	public void onResponse(Call call, Response response) throws IOException {
		if (!response.isSuccessful()) {
			try (ResponseBody body = response.body()) {
				throw new IOException("call to " + call.request().url().url() + " failed. " + body.string());
			}
		}
	}
}
