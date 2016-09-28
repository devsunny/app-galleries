package com.asksunny.batch;

public class IgnoreableExecutionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IgnoreableExecutionException() {		
	}

	public IgnoreableExecutionException(String arg0) {
		super(arg0);		
	}

	public IgnoreableExecutionException(Throwable arg0) {
		super(arg0);
		
	}

	public IgnoreableExecutionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		
	}

	

}
