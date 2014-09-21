package com.asksunny.hadoop.spring;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.core.io.Resource;

public class HDFSResource implements Resource {

	private FileSystem hdfs = null;
	private Path path = null;
	
	public HDFSResource(FileSystem hdfs, String path) {
		this(hdfs, new Path(path));
	}
	public HDFSResource(FileSystem hdfs, Path path) {
		this.hdfs = hdfs;
		this.path = path;
	}
	

	@Override
	public InputStream getInputStream() throws IOException {		
		return hdfs.open(this.path);
	}

	@Override
	public boolean exists() {
		boolean ret = false;
		try{
			ret = hdfs.exists(this.path);
		}catch(IOException ex){
			throw new RuntimeException("HDFS IOException", ex);
		}
		return ret;
	}

	@Override
	public boolean isReadable() {
		
		boolean ret = false;
		try{
			 hdfs.open(this.path).close();
			 ret = true;
		}catch(IOException ex){
			throw new RuntimeException("HDFS IOException", ex);
		}
		return ret;
	}

	@Override
	public boolean isOpen() {		
		return true;
	}

	@Override
	public URL getURL() throws IOException {		
		return null;
	}

	@Override
	public URI getURI() throws IOException {		
		return hdfs.resolvePath(this.path).toUri();
	}

	@Override
	public File getFile() throws IOException {		
		return null;
	}

	
	@Override
	public long contentLength() throws IOException {		
		return hdfs.getContentSummary(this.path).getLength();
	}

	@Override
	public long lastModified() throws IOException {		
		return  hdfs.getFileStatus(this.path).getModificationTime();
	}

	@Override
	public Resource createRelative(String relativePath) throws IOException {	
		throw new IOException("Does not support relativePath");
	}

	@Override
	public String getFilename() {		
		return this.path.getName();
	}

	@Override
	public String getDescription() {		
		return "";
	}

}
