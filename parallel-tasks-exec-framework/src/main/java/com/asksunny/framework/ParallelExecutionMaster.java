package com.asksunny.framework;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.framework.handlers.ParallelTaskAgentHandler;
import com.asksunny.framework.handlers.ParallelTaskClientHandler;
import com.asksunny.framework.handlers.TaskMasterAdminHandler;

public class ParallelExecutionMaster implements ParallelExecutionService, ServerService {

	private static Logger logger = LoggerFactory.getLogger(ParallelExecutionMaster.class);
	private ExecutorService workerExecutor = null;
	private ExecutorService bossExecutor = Executors.newFixedThreadPool(1);
	private SSLServerSocket serverSocket = null;
	public static final int DEFAULT_PORT = 10333;
	public static final int DEFAULT_BACKLOG = 10333;

	private int port = DEFAULT_PORT;
	private int backlog = DEFAULT_BACKLOG;
	private int lookupInterval = 5; // 5 seconds;
	private long startTime = 0;

	private AtomicBoolean stop = new AtomicBoolean(Boolean.FALSE);
	private AtomicBoolean paused = new AtomicBoolean(Boolean.FALSE);

	public ParallelExecutionMaster(int port) {
		this(port, DEFAULT_BACKLOG);
	}

	public ParallelExecutionMaster() {
		this(DEFAULT_PORT, DEFAULT_BACKLOG);
	}

	public ParallelExecutionMaster(int port, int backlog) {
		super();
		this.port = port <= 0 ? DEFAULT_PORT : port;
		this.backlog = backlog <= 0 ? DEFAULT_BACKLOG : backlog;
		this.workerExecutor = Executors.newFixedThreadPool(this.backlog);
		this.startTime = System.currentTimeMillis();
	}

	public void serve() throws Exception {
		SSLContext context = FrameworkTLSContextFactory.createTaskMasterTLSContext();
		ServerSocketFactory serverSocketFactory = context.getServerSocketFactory();
		serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(this.port, this.backlog);
		serverSocket.setNeedClientAuth(true);
		serverSocket.setUseClientMode(false);
		bossExecutor.execute(this);
	}

	public static void main(String[] args) throws Exception {
		ParallelExecutionMaster master = new ParallelExecutionMaster();
		master.serve();
		master.await();
	}

	@Override
	public void run() {
		while (!isStopped()) {
			SSLSocket clientSocket = null;
			try {
				clientSocket = (SSLSocket) serverSocket.accept();
				DataInputStream din = new DataInputStream(clientSocket.getInputStream());
				int clientType = din.readInt();
				int clientMajorVersion = din.readShort();
				int clientMinorVersion = din.readShort();
				logger.info("Client connected version:{}.{}", clientMajorVersion, clientMinorVersion);
				TaskMasterHandler handler = null;
				switch (clientType) {
				case 0:
					logger.info("Task Agent connection:{}", clientSocket.getInetAddress());
					ParallelTaskAgentHandler agentHandler = new ParallelTaskAgentHandler();
					handler = agentHandler;
					break;
				case 1:
					logger.info("Task launcher connection:{}", clientSocket.getInetAddress());
					ParallelTaskClientHandler clientHandler = new ParallelTaskClientHandler();
					handler = clientHandler;
					break;
				case 2:
					logger.info("Task master admin connection:{}", clientSocket.getInetAddress());
					TaskMasterAdminHandler adminHandler = new TaskMasterAdminHandler(this, clientSocket);
					ExecutorService highPriorityExecutor = Executors.newFixedThreadPool(1);
					highPriorityExecutor.execute(adminHandler);
					highPriorityExecutor.shutdown();
					break;
				default:
					logger.warn("Unsupported connection:{}", clientSocket.getInetAddress());
					break;
				}
				if (handler != null && handler instanceof ParallelExecutionClientHandler) {
					handler.init(this, clientSocket);
					this.workerExecutor.execute(handler);
				} else {
					DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
					try {
						dout.write('E');
						dout.writeInt(10000);
						byte[] message = "Invalid client header".getBytes();
						dout.writeInt(message.length);
						dout.flush();
						dout.close();
					} finally {
						clientSocket.close();
					}
				}
			} catch (Throwable t) {
				logger.error("Unhandle client error.", t);
				try {
					clientSocket.close();
				} catch (IOException e) {
					;
				}
			}

		}
	}

	public void shutdownNow() {
		if (this.stop.compareAndSet(false, true)) {
			bossExecutor.shutdownNow();
		}
	}

	public void await() {
		bossExecutor.shutdown();
	}

	public void shutdown() {
		if (this.stop.compareAndSet(false, true)) {
			bossExecutor.shutdown();
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public int getLookupInterval() {
		return lookupInterval;
	}

	public void setLookupInterval(int lookupInterval) {
		this.lookupInterval = lookupInterval;
	}

	public boolean isStopped() {
		return stop.get();
	}

	public ExecutorService getWorkerExecutor() {
		return workerExecutor;
	}

	public void setWorkerExecutor(ExecutorService workerExecutor) {
		this.workerExecutor = workerExecutor;
	}

	public SSLServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(SSLServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	@Override
	public long startTime() {

		return this.startTime;
	}

	@Override
	public long uptime() {
		return System.currentTimeMillis() - this.startTime;
	}

	@Override
	public void pause() {
		if (this.paused.compareAndSet(false, true)) {
			logger.info("ParallelExecutionMaster paused");
		} else {
			logger.info("ParallelExecutionMaster was already paused");
		}

	}

	@Override
	public void resume() {
		if (this.paused.compareAndSet(true, false)) {
			logger.info("ParallelExecutionMaster resumed");
		} else {
			logger.info("ParallelExecutionMaster was already running");
		}

	}

	@Override
	public void stop() {
		if (this.stop.compareAndSet(false, true)) {
			logger.info("ParallelExecutionMaster stopped");
		} else {
			logger.info("ParallelExecutionMaster is terminating gracefully");
		}
	}

}
