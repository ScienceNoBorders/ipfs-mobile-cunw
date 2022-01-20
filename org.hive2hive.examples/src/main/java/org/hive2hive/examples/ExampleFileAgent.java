package org.hive2hive.examples;

import org.hive2hive.core.file.IFileAgent;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Dummy file agent to demonstrate the examples
 * 
 * @author Nico
 *
 */
public class ExampleFileAgent implements IFileAgent {

	private final File root;

	public ExampleFileAgent(String alias) {
		root = new File("/Users/xiaotijun/Downloads/share", alias);
		root.mkdirs();
	}

	public ExampleFileAgent() {
		root = new File("/Users/xiaotijun/Downloads/share", UUID.randomUUID().toString());
		root.mkdirs();
	}

	@Override
	public File getRoot() {
		return root;
	}

	@Override
	public void writeCache(String key, byte[] data) throws IOException {
		// do nothing as examples don't depend on performance
	}

	@Override
	public byte[] readCache(String key) throws IOException {
		// do nothing as examples don't depend on performance
		return null;
	}

}
