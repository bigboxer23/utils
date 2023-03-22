package com.bigboxer23.utils.file;

import java.io.File;

/** Utility class to persist an index to a file on disk and resume from it on restart of program. */
public class FilePersistentIndex extends AbstractFilePersisted {
	private int currentIndex = -1;

	public FilePersistentIndex(String fileName) {
		super(fileName);
		this.filePath = System.getProperty("user.dir") + File.separator + fileName;
		currentIndex = getIndexFromFile();
		if (currentIndex > -1) {
			new File(this.filePath).delete();
			this.filePath = System.getProperty("user.dir") + File.separator + kPrefix + fileName;
			writeToFile(currentIndex);
		}
		this.filePath = System.getProperty("user.dir") + File.separator + kPrefix + fileName;
		currentIndex = getIndexFromFile();
		logger.info(fileName + ": initialized with value " + currentIndex);
	}

	private int getIndexFromFile() {
		try {
			return Integer.parseInt(getStringFromFile());
		} catch (NumberFormatException e) {
			logger.warn("FilePersistentIndex", e);
		}
		return -1;
	}

	public int get() {
		return currentIndex;
	}

	public int reset() {
		currentIndex = -1;
		writeToFile(currentIndex);
		return currentIndex;
	}

	public int increment() {
		currentIndex++;
		return set(currentIndex);
	}

	public int set(int newValue) {
		currentIndex = newValue;
		writeToFile(currentIndex);
		return currentIndex;
	}
}
