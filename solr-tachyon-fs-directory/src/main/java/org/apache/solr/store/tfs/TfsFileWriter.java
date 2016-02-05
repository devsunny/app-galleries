package org.apache.solr.store.tfs;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.lucene.store.OutputStreamIndexOutput;

import tachyon.TachyonURI;
import tachyon.client.TachyonStorageType;
import tachyon.client.UnderStorageType;
import tachyon.client.file.TachyonFileSystem;
import tachyon.client.file.options.OutStreamOptions;
import tachyon.conf.TachyonConf;
import tachyon.exception.TachyonException;

public class TfsFileWriter extends OutputStreamIndexOutput {

	public static final String TFS_SYNC_BLOCK = "solr.tfs.sync.block";
	public static final int BUFFER_SIZE = 16384;

	public TfsFileWriter(TachyonFileSystem fileSystem, TachyonURI path) throws IOException{
		super(path.getPath(), getOutputStream(fileSystem, path), BUFFER_SIZE);
	}

	private static final OutputStream getOutputStream(TachyonFileSystem fileSystem, TachyonURI path)
			throws IOException {
		TachyonConf config = new TachyonConf();		
		OutputStream out = null;
		try {
			OutStreamOptions.Builder builder = new OutStreamOptions.Builder(config);
			builder.setTachyonStorageType(TachyonStorageType.STORE);
			builder.setUnderStorageType(UnderStorageType.SYNC_PERSIST);
			out = fileSystem.getOutStream(path, builder.build());
		} catch (TachyonException e) {
			throw new IOException(e);
		}
		return out;
	}

}
