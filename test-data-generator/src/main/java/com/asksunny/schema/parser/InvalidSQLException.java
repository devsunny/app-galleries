package com.asksunny.schema.parser;

public class InvalidSQLException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidSQLException(String message) {
		super(message);
	}

	public InvalidSQLException(String expect, String encounter, int line, int column) {
		super(String.format("Expect token '%s' but encounter '%s' at %d:%d", expect, encounter, line, column));
	}

}
