package org.dcache.chimera;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;

public class HdfsIoStream {

	private final Path path;
	private final FSDataInputStream fsIn;
	private final FSDataOutputStream fsOut;

	public HdfsIoStream(Path path, FSDataInputStream fsIn) {
		this.path = path;
		this.fsIn = fsIn;
		this.fsOut = null;
	}
	
	public HdfsIoStream(Path path, FSDataOutputStream fsOut) {
		this.path = path;
		this.fsIn = null;
		this.fsOut = fsOut;
	}

	public Path getPath() {
		return path;
	}

	public FSDataInputStream getFsIn() {
		return fsIn;
	}

	public FSDataOutputStream getFsOut() {
		return fsOut;
	}
}
