package com.asksunny.app.domain;

public class ResponseMessage<T> {

	public static final String REASON_OK = "OK";
	public static final int STATUS_OK = 200;
	
	private int status;
	private String reason = REASON_OK;
	private T payload;

	public ResponseMessage() {

	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public T getPayload() {
		return payload;
	}

	public ResponseMessage(int status, String reason, T payload) {
		super();
		this.status = status;
		this.reason = reason;
		this.payload = payload;
	}

	public void setPayload(T payload) {
		this.payload = payload;
	}

}
