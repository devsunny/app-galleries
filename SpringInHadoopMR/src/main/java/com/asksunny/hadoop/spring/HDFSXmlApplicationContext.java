package com.asksunny.hadoop.spring;

import org.apache.hadoop.fs.FileSystem;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;

public class HDFSXmlApplicationContext extends
		AbstractXmlApplicationContext {

	private Resource[] resources = null;

	public HDFSXmlApplicationContext(FileSystem hdfs, String path) {
		super(null);
		setValidating(false);
		resources = new Resource[] { new HDFSResource(hdfs, path) };
		this.refresh();
	}

	public HDFSXmlApplicationContext(FileSystem hdfs, String[] paths) {
		super(null);
		setValidating(false);
		resources = new Resource[paths.length];
		for (int i = 0; i < paths.length; i++) {
			resources[i] = new HDFSResource(hdfs, paths[i]);
		}
		this.refresh();
	}

	@Override
	protected Resource[] getConfigResources() {
		return this.resources;
	}

}
