package com.bigboxer23.utils.file;

/** Utility class to persist an index to a file on disk and resume from it on restart of program. */
public class FilePersistentIndex extends AbstractFilePersisted {
	private int currentIndex = -1;

	public FilePersistentIndex(String fileName) {
		super(fileName);
		currentIndex = getIndexFromFile();
		logger.info(fileName + ": initialized with value " + currentIndex);
	}

	private int getIndexFromFile() {
		try {
			String fileContent = getStringFromFile();
			return fileContent.isEmpty() ? -1 : Integer.parseInt(getStringFromFile());
		} catch (NumberFormatException e) {
			logger.warn("FilePersistentIndex:", e);
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
