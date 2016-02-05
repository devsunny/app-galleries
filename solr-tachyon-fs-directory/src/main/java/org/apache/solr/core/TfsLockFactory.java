package org.apache.solr.core;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.LockReleaseFailedException;
import org.apache.solr.common.util.IOUtils;
import org.apache.solr.store.hdfs.HdfsDirectory;
import org.apache.solr.store.tfs.TfsDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tachyon.TachyonURI;
import tachyon.client.UnderStorageType;
import tachyon.client.file.FileOutStream;
import tachyon.client.file.TachyonFile;
import tachyon.client.file.TachyonFileSystem;
import tachyon.client.file.options.MkdirOptions;
import tachyon.conf.TachyonConf;
import tachyon.exception.FileAlreadyExistsException;
import tachyon.exception.FileDoesNotExistException;
import tachyon.exception.InvalidPathException;
import tachyon.exception.TachyonException;

public class TfsLockFactory extends LockFactory {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static final TfsLockFactory INSTANCE = new TfsLockFactory();

	private TfsLockFactory() {
	}

	@Override
	public Lock obtainLock(Directory dir, String lockName) throws IOException {
		if (!(dir instanceof TfsDirectory)) {
			throw new UnsupportedOperationException(
					"TfsLockFactory can only be used with TfsDirectory subclasses, got: " + dir);
		}
		final TfsDirectory tsDir = (TfsDirectory) dir;
		final TachyonFileSystem fileSystem = tsDir.getFileSystem();
		final TachyonURI path = tsDir.getTfsDirPath();
		TachyonURI lockpath = new TachyonURI(tsDir.getTfsDirPath(), new TachyonURI(lockName));
		FileOutStream fout = null;
		while (true) {
			try {
				TachyonFile tf = fileSystem.openIfExists(path);
				if (tf == null) {
					TachyonConf config = new TachyonConf();
					MkdirOptions.Builder builder = new MkdirOptions.Builder(config);
					builder.setRecursive(true);
					builder.setUnderStorageType(UnderStorageType.SYNC_PERSIST);
					fileSystem.mkdir(path, builder.build());
				}
				fout = fileSystem.getOutStream(lockpath);
				break;
			} catch (TachyonException e) {
				log.warn("Tachyon is not ready and wait for 5 second....", e);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					Thread.interrupted();
				}
				continue;
			} finally {
				org.apache.commons.io.IOUtils.closeQuietly(fout);
			}
		}
		return new TfsLock(fileSystem, lockpath);
	}

	private static final class TfsLock extends Lock {

		private final TachyonFileSystem fileSystem;
		private final TachyonURI locakFile;
		private volatile boolean closed;

		public TfsLock(TachyonFileSystem fileSystem, TachyonURI locakFile) {
			super();
			this.fileSystem = fileSystem;
			this.locakFile = locakFile;
		}

		@Override
		public void close() throws IOException {
			if (closed) {
				return;
			}
			try {
				TachyonFile tf = fileSystem.openIfExists(locakFile);
				if (tf != null) {
					fileSystem.delete(tf);
				}
			} catch (TachyonException e) {
				throw new IOException(e);
			}
		}

		@Override
		public void ensureValid() throws IOException {
			try {
				TachyonFile tf = fileSystem.openIfExists(locakFile);
				if (tf == null) {
					throw new IOException("Invalid lock file:" + locakFile);
				}
			} catch (TachyonException e) {
				throw new IOException(e);
			}
		}

	}

}
