package com.bigboxer23.utils.file;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TestAbstractFilePersisted {

	@TempDir
	File tempDir;

	private TestableAbstractFilePersisted testInstance;
	private String originalUserDir;

	@BeforeEach
	void setUp() {
		originalUserDir = System.getProperty("user.dir");
		System.setProperty("user.dir", tempDir.getAbsolutePath());
		testInstance = new TestableAbstractFilePersisted("testFile");
	}

	@AfterEach
	void tearDown() {
		System.setProperty("user.dir", originalUserDir);
		if (testInstance != null) {
			testInstance.resetFile();
		}
	}

	@Test
	void testConstructor() {
		String expectedPath = tempDir.getAbsolutePath() + File.separator + ".index_testFile";
		assertEquals(expectedPath, testInstance.getFilePath());
	}

	@Test
	void testWriteToFileNoAppend() throws IOException {
		testInstance.writeToFile("test content");

		File file = new File(testInstance.getFilePath());
		assertTrue(file.exists());

		String content = FileUtils.readFileToString(file, Charset.defaultCharset());
		assertEquals("test content", content);
	}

	@Test
	void testWriteToFileWithAppend() throws IOException {
		TestableAbstractFilePersistedAppend appendInstance = new TestableAbstractFilePersistedAppend("appendFile");

		appendInstance.writeToFile("first line");
		appendInstance.writeToFile("second line");

		File file = new File(appendInstance.getFilePath());
		String content = FileUtils.readFileToString(file, Charset.defaultCharset());
		assertEquals("first line\nsecond line\n", content);

		appendInstance.resetFile();
	}

	@Test
	void testResetFile() throws IOException {
		testInstance.writeToFile("test content");
		File file = new File(testInstance.getFilePath());
		assertTrue(file.exists());

		testInstance.resetFile();
		assertFalse(file.exists());
	}

	@Test
	void testGetStringFromFileExists() throws IOException {
		File file = new File(testInstance.getFilePath());
		FileUtils.writeStringToFile(file, "  test content  ", Charset.defaultCharset());

		String result = testInstance.getStringFromFile();
		assertEquals("test content", result);
	}

	@Test
	void testGetStringFromFileNotExists() {
		String result = testInstance.getStringFromFile();
		assertEquals("", result);
	}

	@Test
	void testGetStringFromFileEmpty() throws IOException {
		File file = new File(testInstance.getFilePath());
		FileUtils.writeStringToFile(file, "", Charset.defaultCharset());

		String result = testInstance.getStringFromFile();
		assertEquals("", result);
	}

	@Test
	void testGetStringFromFileWhitespaceOnly() throws IOException {
		File file = new File(testInstance.getFilePath());
		FileUtils.writeStringToFile(file, "   \n\t  ", Charset.defaultCharset());

		String result = testInstance.getStringFromFile();
		assertEquals("", result);
	}

	@Test
	void testWriteToFileOverwrite() throws IOException {
		testInstance.writeToFile("first content");
		testInstance.writeToFile("second content");

		String result = testInstance.getStringFromFile();
		assertEquals("second content", result);
	}

	@Test
	void testFilePathPrefix() {
		assertTrue(testInstance.getFilePath().contains(".index_"));
	}

	static class TestableAbstractFilePersisted extends AbstractFilePersisted {
		public TestableAbstractFilePersisted(String fileName) {
			super(fileName);
		}

		public String getFilePath() {
			return filePath;
		}
	}

	static class TestableAbstractFilePersistedAppend extends AbstractFilePersisted {
		public TestableAbstractFilePersistedAppend(String fileName) {
			super(fileName);
		}

		public String getFilePath() {
			return filePath;
		}

		@Override
		protected boolean append() {
			return true;
		}
	}
}
