package com.asksunny.batch.graph;

public class ExceptionHolder {

	private Thread causeThread;
	private Throwable uncaughtException;

	public ExceptionHolder() {

	}

	public ExceptionHolder(Thread causeThread, Throwable uncaughtException) {
		super();
		this.causeThread = causeThread;
		this.uncaughtException = uncaughtException;
	}

	public Thread getCauseThread() {
		return causeThread;
	}

	public void setCauseThread(Thread causeThread) {
		this.causeThread = causeThread;
	}

	public Throwable getUncaughtException() {
		return uncaughtException;
	}

	public void setUncaughtException(Throwable uncaughtException) {
		this.uncaughtException = uncaughtException;
	}

}
