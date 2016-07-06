package com.asksunny.framework;

import java.net.Socket;

public interface TaskMasterHandler extends Runnable {

	void init(ParallelExecutionService peService, Socket clientSocket);
	
}
