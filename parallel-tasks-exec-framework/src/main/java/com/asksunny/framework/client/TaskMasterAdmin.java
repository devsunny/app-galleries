package com.asksunny.framework.client;

import java.io.DataOutputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.framework.FrameworkTLSContextFactory;

public class TaskMasterAdmin {
	private static Logger logger = LoggerFactory.getLogger(TaskMasterAdmin.class);
	public static final int DEFAULT_PORT = 10333;
	public static final int CLIENT_TYPE = 2;
	public static final short CLIENT_MAJOR_VERSION = 0;
	public static final short CLIENT_MINOR_VERSION = 1;
	private String taskMasterHost;
	private int taskMasterPort;
	
	public TaskMasterAdmin() {
		this("localhost", DEFAULT_PORT);
	}

	public TaskMasterAdmin(String taskMasterHost, int taskMasterPort) {
		super();
		this.taskMasterHost = taskMasterHost;
		this.taskMasterPort = taskMasterPort;
	}
	
	public void connect() throws Exception
	{		
		SSLContext context = FrameworkTLSContextFactory.createTaskAgentTLSContext();
		SSLSocketFactory socketFactory = context.getSocketFactory();
		SSLSocket serverSocket = (SSLSocket)socketFactory.createSocket(this.taskMasterHost, this.taskMasterPort);
		DataOutputStream dout = new DataOutputStream(serverSocket.getOutputStream());
		dout.writeInt(CLIENT_TYPE);
		dout.writeShort(CLIENT_MAJOR_VERSION);
		dout.writeShort(CLIENT_MINOR_VERSION);
		dout.flush();		
	}



	public static void main(String[] args) throws Exception
	{
		
		TaskMasterAdmin launcher = new TaskMasterAdmin();
		launcher.connect();
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
