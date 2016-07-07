package com.asksunny.framework.handlers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.framework.ParallelExecutionClientHandler;
import com.asksunny.framework.ParallelExecutionService;
import com.asksunny.tasks.ParallePartitioner;
import com.asksunny.tasks.ParallelTask;

public class ParallelTaskClientHandler implements ParallelExecutionClientHandler {
	private static Logger logger = LoggerFactory.getLogger(ParallelTaskClientHandler.class);
	private DataOutputStream dout = null;
	private DataInputStream din = null;
	private Socket clientSocket = null;
	private ParallelExecutionService peService = null;

	public ParallelTaskClientHandler() {
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
		logger.debug("Init completed.");
	}

	@Override
	public void run() {

		try {
			int indicator = -1;
			while ((indicator = din.read()) != -1) {

				switch (indicator) {
				case 'R':
					String className = din.readUTF();
					logger.info("TaskClass:{}", className);
					int len = din.readInt();
					String[] params = new String[len];
					for (int i = 0; i < len; i++) {
						params[i] = din.readUTF();
					}
					logger.info("{} with params {}.", className, Arrays.asList(params));
					handleRequest(className, params);
					break;
				case 'X':
					close();
					return;
				default:

					break;
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				close();
			} catch (Exception e) {
				;
			}
		}

	}

	protected void handleRequest(String className, String[] args) {
		
		try {
			Object obj = Class.forName(className).newInstance();
			ParallelTask task = (ParallelTask) obj;
			if(task.init(args)){
				ParallePartitioner partitioner = task.getParallePartitioner();
				List<String[]> partArgs = partitioner.doPartition();
				for (String[] strings : partArgs) {
					logger.debug("SubTask ARGS:{}", Arrays.asList(strings));
				}
				
			}else{
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

	public void close() throws IOException {
		try {
			din.close();
		} finally {
			try {
				dout.close();
			} finally {
				clientSocket.close();
			}
		}

	}

}
