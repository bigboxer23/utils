package com.bigboxer23.utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** */
public abstract class AbstractFilePersisted {
	protected static final String kPrefix = ".index_";

	protected String filePath;

	protected static final Logger logger = LoggerFactory.getLogger(AbstractFilePersisted.class);

	protected AbstractFilePersisted(String fileName) {
		this.filePath = System.getProperty("user.dir") + File.separator + kPrefix + fileName;
	}

	protected void writeToFile(Object objectToWrite) {
		try {
			FileUtils.writeStringToFile(new File(this.filePath), objectToWrite + "", Charset.defaultCharset(), false);
		} catch (IOException e) {
			logger.warn("FilePersistentIndex:writeToFile", e);
		}
	}

	protected String getStringFromFile() {
		try {
			return FileUtils.readFileToString(new File(this.filePath), Charset.defaultCharset())
					.trim();
		} catch (IOException e) {
			logger.warn("AbstractFilePersisted", e);
		}
		return "";
	}
}
