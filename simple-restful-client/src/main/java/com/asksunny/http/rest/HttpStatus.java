package com.asksunny.http.rest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpStatus {

	private static final Pattern SEPARATOR = Pattern.compile("\\s+");
	private String httpVersion;
	private int statusCode;
	private String message;

	public HttpStatus(String statusLine) {
		init(statusLine);
	}

	private void init(String statusLine) {
		Matcher matcher = SEPARATOR.matcher(statusLine);
		int pos = 0;
		int s = 0;
		while (matcher.find()) {
			int e = matcher.start();
			switch (pos) {
			case 0:
				this.httpVersion = statusLine.substring(s, e);
				break;
			case 1:				
				this.statusCode = Integer.valueOf(statusLine.substring(s, e));
				if (matcher.end() < statusLine.length()) {
					this.message = statusLine.substring(matcher.end());
				}
			default:
				return;
			}
			pos++;
			s = matcher.end();
		}
	}
	
	
	public String getHttpVersion() {
		return httpVersion;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getMessage() {
		return message;
	}

}
