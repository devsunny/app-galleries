package org.apache.solr.store.tfs;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.store.BaseDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.LockFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tachyon.TachyonURI;
import tachyon.client.file.TachyonFileSystem;
import tachyon.client.file.TachyonFileSystem.TachyonFileSystemFactory;
import tachyon.client.file.options.DeleteOptions;
import tachyon.conf.TachyonConf;
import tachyon.exception.TachyonException;
import tachyon.thrift.FileInfo;

public class TfsDirectory extends BaseDirectory {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static final int BUFFER_SIZE = 8192;
	private static final String LF_EXT = ".lf";
	protected final TachyonURI tfsDirPath;
	private final TachyonFileSystem fileSystem;

	public TfsDirectory(TachyonFileSystem fileSystem, TachyonURI tfsDirPath, LockFactory lockFactory) {
		super(lockFactory);
		this.tfsDirPath = tfsDirPath;
		this.fileSystem = fileSystem;
	}

	public TachyonURI getTfsDirPath() {
		return tfsDirPath;
	}

	public TachyonFileSystem getFileSystem() {
		return fileSystem;
	}

	@Override
	public String[] listAll() throws IOException {
		List<String> filesNames = new ArrayList<String>();
		try {
			List<FileInfo> files = this.fileSystem.listStatus(this.fileSystem.open(tfsDirPath));
			if (files != null) {
				for (FileInfo fileInfo : files) {
					filesNames.add(toNormalName(fileInfo.getName()));
				}
			}
		} catch (TachyonException e) {
			throw new IOException(e);
		}
		return filesNames.toArray(new String[filesNames.size()]);
	}

	@Override
	public void deleteFile(String name) throws IOException {
		TachyonURI tgtFile = new TachyonURI(this.tfsDirPath, new TachyonURI(name));
		LOG.debug("Deleting {}", tgtFile.getPath());
		try {
			this.fileSystem.delete(this.fileSystem.open(tgtFile), DeleteOptions.defaults());
		} catch (TachyonException e) {
			throw new IOException(e);
		}
	}

	@Override
	public long fileLength(String name) throws IOException {

		TachyonURI tgtFile = new TachyonURI(this.tfsDirPath, new TachyonURI(name));
		LOG.debug("fileLength {}", tgtFile.getPath());
		try {
			FileInfo finfo = this.fileSystem.getInfo(this.fileSystem.open(tgtFile));
			return finfo.length;
		} catch (TachyonException e) {
			throw new IOException(e);
		}
	}

	@Override
	public IndexOutput createOutput(String name, IOContext context) throws IOException {
		LOG.debug("Creat INDEX output: {}", name);
		return new TfsFileWriter(fileSystem, new TachyonURI(this.tfsDirPath, new TachyonURI(name)));
	}

	@Override
	public void sync(Collection<String> names) throws IOException {
		LOG.debug("Sync called on {}", Arrays.toString(names.toArray()));
	}

	@Override
	public void renameFile(String source, String dest) throws IOException {
		TachyonURI srcFile = new TachyonURI(this.tfsDirPath, new TachyonURI(source));
		TachyonURI destFile = new TachyonURI(this.tfsDirPath, new TachyonURI(dest));
		try {
			this.fileSystem.rename(this.fileSystem.open(srcFile), destFile);
		} catch (TachyonException e) {
			throw new IOException(e);
		}
	}

	@Override
	public IndexInput openInput(String name, IOContext context) throws IOException {
		TachyonURI srcFile = new TachyonURI(this.tfsDirPath, new TachyonURI(name));
		return new TfsIndexInput(name, fileSystem, srcFile, BUFFER_SIZE);
	}

	@Override
	public void close() throws IOException {
		LOG.info("Closing hdfs directory {}", tfsDirPath);
		isOpen = false;
	}

	public boolean isClosed() {
		return !isOpen;
	}

	protected String[] getNormalNames(List<String> files) {
		int size = files.size();
		for (int i = 0; i < size; i++) {
			String str = files.get(i);
			files.set(i, toNormalName(str));
		}
		return files.toArray(new String[] {});
	}

	protected String toNormalName(String name) {
		if (name.endsWith(LF_EXT)) {
			return name.substring(0, name.length() - 3);
		}
		return name;
	}

}
