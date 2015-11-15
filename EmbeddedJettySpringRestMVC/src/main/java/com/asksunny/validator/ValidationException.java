package com.asksunny.validator;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ValidationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StringBuffer buffer = new StringBuffer();

	public ValidationException() {
		super("ValidationException");
	}

	public void appendError(Class<?> clazz, String fieldName, String message) {
		buffer.append(String.format("%s.%s validation failed:%s\n", clazz.getName(), fieldName, message));
	}

	@Override
	public String getLocalizedMessage() {
		return buffer.toString();
	}

	@Override
	public String getMessage() {
		return buffer.toString();
	}

	@Override
	public String toString() {
		return buffer.toString();
	}

	@Override
	public void printStackTrace(PrintStream s) {
		s.println(buffer.toString());
		super.printStackTrace(s);
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		s.println(buffer.toString());
		super.printStackTrace(s);
	}

}
