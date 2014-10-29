package com.asksunny.io.sftp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.asksunny.io.IPHostInfo;
import com.asksunny.io.URIInfo;
import com.asksunny.io.URIParser;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SFTPOutputStream extends OutputStream {

	private Session session = null;
	private OutputStream remoteOut = null;
	private ChannelSftp sftpChannel = null;

	public SFTPOutputStream(String sftpUri) throws IOException {
		this(sftpUri, null, null);
	}

	public SFTPOutputStream(String sftpUri, String pathToKey)
			throws IOException {
		this(sftpUri, null, pathToKey);
	}

	public SFTPOutputStream(String sftpUri, String username, String pathToKey)
			throws IOException {

		URIInfo infos = URIParser.parse(sftpUri);
		if (infos.getProtocol() == null
				|| !infos.getProtocol().equalsIgnoreCase("sftp")) {
			throw new IOException("not SFTP protocol");
		}

		if (infos.getHostinfos().size() != 1) {
			throw new IOException(
					"Please use SFTPMulticastOutputStream for multihost upload");
		}

		IPHostInfo iphostInfo = infos.getHostinfos().get(0);
		String key = pathToKey == null ? iphostInfo.getPassword() : pathToKey;

		String user = username == null ? iphostInfo.getUsername() : username;
		if (user == null) {
			user = System.getProperty("user.name");
		}
		
		initSFTPOutputStream(iphostInfo.getHostname(), iphostInfo.getPort(),
				infos.getDirectory(), infos.getFilename(), user, key);

	}

	public SFTPOutputStream(String host, int port, String directory,
			String filename, String username, String pathToKey)
			throws IOException {
		initSFTPOutputStream(host, port, directory, filename, username,
				pathToKey);
	}

	protected void initSFTPOutputStream(String host, int port,
			String directory, String filename, String username, String pathToKey)
			throws IOException {
		try {
			JSch jsch = new JSch();
			File keyFile = null;
			String password = null;
			if (pathToKey == null) {
				keyFile = new File(System.getProperty("user.home"),
						".ssh/id_rsa");
			} else {
				keyFile = new File(pathToKey);
				if (keyFile.isFile() && keyFile.canRead()) {
					;
				} else {
					password = pathToKey;
					keyFile = null;
				}

			}
			if (keyFile != null) {
				jsch.addIdentity(keyFile.getAbsolutePath());
			}
			session = (port <= 0 || port == 22) ? jsch.getSession(username,
					host) : jsch.getSession(username, host, port);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			if (password != null && keyFile == null) {
				session.setPassword(password);
			}
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;
			
			if(directory!=null && directory.length()>0){
				directory = directory.replaceAll("\\\\", "/");
				sftpChannel.cd(directory);
			}			
			remoteOut = sftpChannel.put(filename, ChannelSftp.OVERWRITE);
		} catch (Exception e) {
			throw new IOException("Failed to connect to remote server", e);
		}
	}

	@Override
	public void write(int b) throws IOException {
		remoteOut.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		remoteOut.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		remoteOut.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		remoteOut.flush();
	}

	@Override
	public void close() throws IOException {
		try {
			try {
				if (this.remoteOut != null)
					remoteOut.close();
			} finally {
				try {
					if (this.sftpChannel != null)
						this.sftpChannel.disconnect();
				} finally {
					if (this.session != null)
						this.session.disconnect();
				}
			}
		} catch (Exception e) {
			throw new IOException("Failed to disconnect from SFTP server.", e);
		}
	}

}
