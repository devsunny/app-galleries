package com.asksunny.io;

import java.util.ArrayList;
import java.util.List;

public class URIInfo {

	private String protocol;
	
	private List<IPHostInfo> hostinfos = new ArrayList<IPHostInfo>();
	private String path;
	private String filename;
	private String directory;
	
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public List<IPHostInfo> getHostinfos() {
		return hostinfos;
	}

	public void setHostinfos(List<IPHostInfo> hostinfos) {
		this.hostinfos = hostinfos;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

}
