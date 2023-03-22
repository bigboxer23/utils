package com.bigboxer23.utils.file;

/**
 * Simple get/set wrapper around a string value which is persisted to the file system. On
 * initialization, value is read from file system into memory
 */
public class FilePersistedString extends AbstractFilePersisted {
	private String content;

	public FilePersistedString(String fileName) {
		super(fileName);
		content = getStringFromFile();
		logger.info(fileName + ": initialized with value " + content);
	}

	public String get() {
		return content;
	}

	public void set(String content) {
		this.content = content;
		writeToFile(this.content);
	}
}
