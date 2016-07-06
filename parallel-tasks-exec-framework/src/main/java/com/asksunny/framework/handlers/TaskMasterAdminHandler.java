package com.asksunny.framework.handlers;

import java.net.Socket;

import com.asksunny.framework.ServerService;

public class TaskMasterAdminHandler implements Runnable {

	private ServerService serverService;
	private Socket clientSocket;
	
	

	public TaskMasterAdminHandler(ServerService serverService, Socket clientSocket) {
		super();
		this.serverService = serverService;
		this.clientSocket = clientSocket;
	}



	@Override
	public void run() 
	{
		

	}



	public ServerService getServerService() {
		return serverService;
	}



	public void setServerService(ServerService serverService) {
		this.serverService = serverService;
	}



	public Socket getClientSocket() {
		return clientSocket;
	}



	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

}
