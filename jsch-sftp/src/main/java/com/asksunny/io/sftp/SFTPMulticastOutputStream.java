package com.asksunny.io.sftp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.asksunny.io.IPHostInfo;
import com.asksunny.io.URIInfo;
import com.asksunny.io.URIParser;

public class SFTPMulticastOutputStream extends OutputStream {

	private List<SFTPOutputStream> remoteOuts = new ArrayList<SFTPOutputStream>();

	
	public SFTPMulticastOutputStream(String sftpUri) throws IOException {
		this(sftpUri, null, null);
	}
	
	public SFTPMulticastOutputStream(String sftpUri, String pathToKey) throws IOException {
		this(sftpUri, null, pathToKey);
	}
	
	public SFTPMulticastOutputStream(String sftpUri, String username,
			String pathToKey) throws IOException {

		URIInfo infos = URIParser.parse(sftpUri);
		if (infos.getProtocol() == null
				|| !infos.getProtocol().equalsIgnoreCase("sftp")) {
			throw new IOException("");
		}

		for (IPHostInfo iphostInfo : infos.getHostinfos()) {
			String key = pathToKey == null ? iphostInfo.getPassword()
					: pathToKey;			
			
			String user = username==null?iphostInfo.getUsername():username;
			if(user==null){
				user = System.getProperty("user.name");
			}
			
			
			SFTPOutputStream out = new SFTPOutputStream(
					iphostInfo.getHostname(), iphostInfo.getPort(),
					infos.getDirectory(), infos.getFilename(),
					user, key);
			remoteOuts.add(out);
		}

	}

	public static void main(String[] args) throws IOException {
		System.setProperty("user.home", "C:/cygwin64/home/SunnyLiu");
		SFTPOutputStream out = new SFTPOutputStream(
				"sftp://sliu@192.168.1.18/home/sliu/LastTest.txt");
		out.write("Hello Sunny Liu, more stuff blala\n".getBytes());
		out.flush();
		out.close();
	}

	@Override
	public void write(int b) throws IOException {
		for (SFTPOutputStream out : this.remoteOuts) {
			out.write(b);
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		for (SFTPOutputStream out : this.remoteOuts) {
			out.write(b);
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (SFTPOutputStream out : this.remoteOuts) {
			out.write(b, off, len);
		}
	}

	@Override
	public void flush() throws IOException {
		for (SFTPOutputStream out : this.remoteOuts) {
			out.flush();
		}
	}

	@Override
	public void close() throws IOException {
		IOException t = null;
		for (SFTPOutputStream out : this.remoteOuts) {
			try {
				out.close();
			} catch (IOException iOException) {
				t = iOException;
			}
		}
		if (t != null)
			throw t;
	}

}
