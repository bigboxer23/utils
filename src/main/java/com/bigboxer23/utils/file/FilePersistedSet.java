package com.bigboxer23.utils.file;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/** */
public class FilePersistedSet extends AbstractFilePersisted {
	private Set<String> set;

	public FilePersistedSet(String fileName) {
		super(fileName);
		initSetFromFile();
		logger.info(fileName + ": initialized with values " + set.size());
	}

	private void initSetFromFile() {
		set = new HashSet<>();
		String content = getStringFromFile();
		if (content.length() > 0) {
			String[] contentArray = content.split("\n");
			set.addAll(Arrays.asList(contentArray));
		}
	}

	@Override
	protected boolean append() {
		return true;
	}

	public boolean contains(String content) {
		return set.contains(content.replace("\n", "###"));
	}

	public void add(String content) {
		set.add(content.replace("\n", "###"));
		writeToFile(content.replace("\n", "###"));
	}

	public int size() {
		return set.size();
	}

	public void addAll(Collection<String> contents) {
		contents.forEach(this::add);
	}

	public void reset() {
		resetFile();
		initSetFromFile();
	}
}
