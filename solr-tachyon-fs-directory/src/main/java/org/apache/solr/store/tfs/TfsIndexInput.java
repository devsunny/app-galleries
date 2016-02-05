package org.apache.solr.store.tfs;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.store.IndexInput;
import org.apache.solr.store.blockcache.CustomBufferedIndexInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tachyon.TachyonURI;
import tachyon.client.file.FileInStream;
import tachyon.client.file.FileOutStream;
import tachyon.client.file.TachyonFile;
import tachyon.client.file.TachyonFileSystem;
import tachyon.exception.FileAlreadyExistsException;
import tachyon.exception.InvalidPathException;
import tachyon.exception.TachyonException;

public class TfsIndexInput extends IndexInput {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final TachyonURI path;
	private final FileInStream inputStream;
	private final TachyonFileSystem fileSystem;
	private final long length;
	private boolean clone = false;
	private long position = 0;
	private int bz;

	public TfsIndexInput(String name, TachyonFileSystem fileSystem, TachyonURI path, int bufferSize)
			throws IOException {
		super(name);
		this.path = path;
		this.fileSystem = fileSystem;
		this.bz = bufferSize;
		try {
			TachyonFile file = fileSystem.open(path);
			System.out.println(path.getPath());
			this.length = fileSystem.getInfo(file).length;
			System.out.println(this.length);
			this.inputStream = fileSystem.getInStream(file);			
		} catch (TachyonException e) {
			throw new IOException(e);
		}
	}

	@Override
	public long length() {
		return this.length;
	}

	public boolean isClone() {
		return clone;
	}

	public void setClone(boolean clone) {
		this.clone = clone;
	}

	public TachyonURI getPath() {
		return path;
	}

	public FileInStream getInputStream() {
		return inputStream;
	}

	public long getLength() {
		return length;
	}

	@Override
	public void close() throws IOException {
		this.inputStream.close();

	}

	@Override
	public long getFilePointer() {
		LOG.debug("GET File Ponter:" + this.position);
		return this.position;
	}

	@Override
	public void seek(long pos) throws IOException {
		LOG.debug("seek File Ponter:" + pos);
		this.inputStream.seek(pos);
		this.position = pos;
	}

	@Override
	public IndexInput slice(String sliceDescription, long offset, long length) throws IOException {
		try {
			TachyonURI pathslice = new TachyonURI(path.getParent(), new TachyonURI(sliceDescription));
			FileOutStream fout = fileSystem.getOutStream(pathslice);
			try {
				seek(offset);
				long tobecopy = length;
				int cp = 0;
				byte[] buf = new byte[this.bz];
				while (tobecopy > 0 && (cp = this.inputStream.read(buf)) > -1) {
					fout.write(buf, 0, cp);
					tobecopy = tobecopy - cp;
				}
				this.position = this.position + length;
				fout.flush();
			} finally {
				fout.close();
			}
			return new TfsIndexInput(sliceDescription, fileSystem, pathslice, this.bz);
		} catch (TachyonException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}

	@Override
	public byte readByte() throws IOException {
		LOG.debug("readByte File Ponter:");
		int b = this.inputStream.read();
		if (b != -1) {
			position++;
		}
		return (byte) b;
	}

	@Override
	public void readBytes(byte[] b, int offset, int len) throws IOException {
		LOG.debug("readBytes [{}]", len);
		int lenx = 0;
		int rlen = 0;
		int os = offset;
		while (rlen < len) {
			lenx = this.inputStream.read(b, os, len - rlen);
			os = os + lenx;
			rlen = rlen + lenx;
		}
		this.position = this.position + len;
	}

}
