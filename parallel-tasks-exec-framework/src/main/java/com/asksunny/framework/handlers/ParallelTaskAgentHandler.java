package com.asksunny.framework.handlers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.asksunny.framework.ParallelExecutionClientHandler;
import com.asksunny.framework.ParallelExecutionService;

public class ParallelTaskAgentHandler implements ParallelExecutionClientHandler {

	private DataOutputStream dout = null;
	private DataInputStream din = null;
	private Socket clientSocket = null;
	private ParallelExecutionService peService = null;

	public ParallelTaskAgentHandler() {

	}

	@Override
	public void init(ParallelExecutionService peService, Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.peService = peService;
		try {
			dout = new DataOutputStream(clientSocket.getOutputStream());
			dout.write('R');
			dout.flush();
			din = new DataInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException("Failed to handshake with client", e);
		}
	}

	@Override
	public void run() {
		try {
			int indicattor = din.read();
			switch (indicattor) {
			case 'R':
				//Synchronous Task
				break;
			case 'A':
				//Fire and Go
				
				break;
			default:

				break;
			}

		} catch (Throwable e) {
			;
		}

	}

}
