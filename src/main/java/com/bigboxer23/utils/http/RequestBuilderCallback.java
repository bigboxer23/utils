package com.bigboxer23.utils.http;

import okhttp3.Request;

/** Give copy of builder so can add header or other attributes */
public interface RequestBuilderCallback {
	public Request.Builder modifyBuilder(Request.Builder builder);
}
