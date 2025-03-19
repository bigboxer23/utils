package com.bigboxer23.utils.command;

import java.io.IOException;

/** */
@FunctionalInterface
public interface VoidCommand {
	void execute() throws IOException, InterruptedException;
}
