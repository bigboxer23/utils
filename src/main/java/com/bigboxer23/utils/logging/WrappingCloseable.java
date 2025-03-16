package com.bigboxer23.utils.logging;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/** */
public class WrappingCloseable implements Closeable {
	List<Closeable> closeables = new ArrayList<>();

	public WrappingCloseable(Closeable... closeables) {
		if (closeables != null) {
			this.closeables.addAll(Arrays.asList(closeables));
		}
	}

	public WrappingCloseable(Collection<Closeable> closeables) {
		if (closeables != null) {
			this.closeables.addAll(closeables);
		}
	}

	@Override
	public void close() throws IOException {
		for (Closeable closeable : closeables) {
			closeable.close();
		}
	}
}
