package com.asksunny.http.rest;

public class HttpException extends RuntimeException {

	
	int statusCode;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HttpException(Throwable arg0) {
		super(arg0);
	}

	public HttpException(String arg0, Throwable arg1) {
		super(arg0, arg1);		
	}

	public HttpException(String arg0, int status) {
		super(arg0);	
		this.statusCode = status;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	@Override
	public String toString() {
		return "HttpException statusCode=" + statusCode + ", " + super.toString();
	}

	

}
