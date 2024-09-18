package com.bigboxer23.utils.file;

/** */
public class FilePersistedBoolean extends AbstractFilePersisted {
	private boolean persisted = false;

	public FilePersistedBoolean(String fileName) {
		super(fileName);
		persisted = getBooleanFromFile();
		logger.info(fileName + ": initialized with value " + persisted);
	}

	private boolean getBooleanFromFile() {
		try {
			String fileContent = getStringFromFile();
			return !fileContent.isEmpty() && Boolean.parseBoolean(getStringFromFile());
		} catch (Exception e) {
			logger.warn("FilePersistentIndex:", e);
		}
		return false;
	}

	public boolean get() {
		return persisted;
	}

	public boolean set(boolean newValue) {
		persisted = newValue;
		writeToFile(persisted);
		return persisted;
	}
}
