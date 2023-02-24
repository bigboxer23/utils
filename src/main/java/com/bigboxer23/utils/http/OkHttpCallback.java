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
	public final void onResponse(Call call, Response response) throws IOException {
		try (ResponseBody body = response.body()) {
			if (!response.isSuccessful()) {
				throw new IOException("call to " + call.request().url().url() + " failed. " + body.string());
			}
			onResponseBody(call, body);
		}
	}

	/**
	 * Stub so can do things with body, automatically deal w/closing body
	 *
	 * @param call
	 * @param responseBody
	 */
	public void onResponseBody(Call call, ResponseBody responseBody) {
		// Stub
		try {
			onResponseBodyString(call, responseBody.string());
		} catch (IOException e) {
			logger.warn("onResponseBody: ", e);
		}
	}

	public void onResponseBodyString(Call call, String stringBody) {
		// Stub
	}
}
