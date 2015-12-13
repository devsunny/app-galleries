package com.asksunny.fs;

final class IteratorAbortException extends RuntimeException {

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
