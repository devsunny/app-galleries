package com.asksunny.compress;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

import com.asksunny.io.FileSpliter;

public class FileBlockParallelCompressor extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String file;
	private InputStream bin;
	private String out;
	private String compressionType;
	private String encryptionKey;
	private int parallelism = 4;
	private long blockSize = 1024 * 1024 * 64;

		
	public FileBlockParallelCompressor(String file, InputStream bin,
			String out, String compressionType, String encryptionKey) {
		super();
		this.file = file;
		this.bin = bin;
		this.out = out;
		this.compressionType = compressionType;
		this.encryptionKey = encryptionKey;
	}


	public FileBlockParallelCompressor(String file, InputStream bin,
			String out, String compressionType, String encryptionKey,
			int parallelism) {
		super();
		this.file = file;
		this.bin = bin;
		this.out = out;
		this.compressionType = compressionType;
		this.encryptionKey = encryptionKey;
		this.parallelism = parallelism;
	}


	public FileBlockParallelCompressor(String file, InputStream bin) {
		super();
		this.file = file;
		this.bin = bin;
	}
	
	
	@Override
	protected void compute() {
		if (getBin() == null) {
			try {
				InputStream[] ins = FileSpliter.splitInputStream(getFile(),
						getParallelism(), getBlockSize());
				
				ArrayList<FileBlockParallelCompressor>  processors = new ArrayList<FileBlockParallelCompressor>();
				for (int i = 0; i < ins.length; i++) {
					FileBlockParallelCompressor compressor = new FileBlockParallelCompressor(getFile(), ins[i]);
					processors.add(compressor);				
				}				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}else{
			
			
		}

	}

	public int getParallelism() {
		return parallelism;
	}

	public void setParallelism(int parallelism) {
		this.parallelism = parallelism;
	}

	public long getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(long blockSize) {
		this.blockSize = blockSize;
	}

	

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public InputStream getBin() {
		return bin;
	}

	public void setBin(InputStream bin) {
		this.bin = bin;
	}

}
