package com.bigboxer23.utils.http;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 *
 */
public class OkHttpCallback implements Callback
{
	private static final Logger logger = LoggerFactory.getLogger(OkHttpCallback.class);

	@Override
	public void onFailure(Call call, IOException e)
	{
		logger.warn("call to " + call.request().url().url() + " failed.", e);
	}

	@Override
	public void onResponse(Call call, Response response) throws IOException
	{
		if (!response.isSuccessful())
		{
			throw new IOException("call to " + call.request().url().url() + " failed. " + response.body().string());
		}
	}
}
