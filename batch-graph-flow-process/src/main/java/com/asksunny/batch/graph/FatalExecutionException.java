package com.asksunny.batch.graph;

public class FatalExecutionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FatalExecutionException() {		
	}

	public FatalExecutionException(String arg0) {
		super(arg0);		
	}

	public FatalExecutionException(Throwable arg0) {
		super(arg0);
		
	}

	public FatalExecutionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		
	}

	

}
