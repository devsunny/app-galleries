package com.asksunny.framework.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.framework.FrameworkTLSContextFactory;

public class TaskLauncher {
	private static Logger logger = LoggerFactory.getLogger(TaskLauncher.class);
	public static final int DEFAULT_PORT = 10333;
	public static final int CLIENT_TYPE = 1;
	public static final short CLIENT_MAJOR_VERSION = 1;
	public static final short CLIENT_MINOR_VERSION = 0;
	private String taskMasterHost;
	private int taskMasterPort;
	private SSLSocket serverSocket = null;
	private DataOutputStream dout = null;
	private DataInputStream din = null;

	public TaskLauncher() {
		this("localhost", DEFAULT_PORT);
	}

	public TaskLauncher(String taskMasterHost, int taskMasterPort) {
		super();
		this.taskMasterHost = taskMasterHost;
		this.taskMasterPort = taskMasterPort;
	}

	public void connect() throws Exception {
		SSLContext context = FrameworkTLSContextFactory.createTaskAgentTLSContext();
		SSLSocketFactory socketFactory = context.getSocketFactory();
		serverSocket = (SSLSocket) socketFactory.createSocket(this.taskMasterHost, this.taskMasterPort);
		dout = new DataOutputStream(serverSocket.getOutputStream());
		dout.writeInt(CLIENT_TYPE);
		dout.writeShort(CLIENT_MAJOR_VERSION);
		dout.writeShort(CLIENT_MINOR_VERSION);
		dout.flush();
		din = new DataInputStream(serverSocket.getInputStream());
		int indicator = din.read();
		if (indicator != 'R') {
			throw new Exception("Failed to perform handshake for Task launcher");
		}
	}

	public void launchTask(String[] args) throws Exception {

		dout.write('R');
		dout.writeInt(args[0].length());
		dout.write(args[0].getBytes());
		for (int i = 1; i < args.length; i++) {
			dout.writeInt(args[i].length());
			dout.write(args[i].getBytes());
		}
		dout.flush();
	}
	
	
	public void await()  throws Exception 
	{
		din.read();
	}
	

	public static void main(String[] args) throws Exception {

		TaskLauncher launcher = new TaskLauncher();
		launcher.connect();
		launcher.launchTask(args);
		launcher.await();

	}

	public String getTaskMasterHost() {
		return taskMasterHost;
	}

	public void setTaskMasterHost(String taskMasterHost) {
		this.taskMasterHost = taskMasterHost;
	}

	public int getTaskMasterPort() {
		return taskMasterPort;
	}

	public void setTaskMasterPort(int taskMasterPort) {
		this.taskMasterPort = taskMasterPort;
	}

}
