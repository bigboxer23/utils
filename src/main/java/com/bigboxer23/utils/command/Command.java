package com.bigboxer23.utils.command;

import java.io.IOException;

/** */
@FunctionalInterface
public interface Command<T> {
	T execute() throws IOException;
}
