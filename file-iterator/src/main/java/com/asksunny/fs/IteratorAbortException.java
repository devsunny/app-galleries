package com.asksunny.fs;

class IteratorAbortException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	IteratorAbortException() {		
	}

	IteratorAbortException(String message) {
		super(message);		
	}
	

}
