package com.asksunny.framework;

public class TaskCreationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TaskCreationException() {		
	}

	public TaskCreationException(String arg0) {
		super(arg0);		
	}

	public TaskCreationException(Throwable arg0) {
		super(arg0);		
	}

	public TaskCreationException(String arg0, Throwable arg1) {
		super(arg0, arg1);		
	}


}
