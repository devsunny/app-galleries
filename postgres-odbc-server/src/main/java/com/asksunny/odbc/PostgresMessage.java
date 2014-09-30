package com.asksunny.odbc;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;

public class PostgresMessage implements ReferenceCounted {

	private int messageType;
	private ByteBuf message;
	private int referenceCount = 0;

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;

	}

	public ByteBuf getMessage() {
		return message;
	}

	public void setMessage(ByteBuf message) {
		this.message = message;
	}

	public PostgresMessage(int messageType, ByteBuf message) {
		super();
		this.messageType = messageType;
		this.message = message;
	}

	public PostgresMessage() {
		super();
	}

	@Override
	public int refCnt() {
		return this.referenceCount;
	}

	@Override
	public boolean release() {
		return release(1);
	}

	@Override
	public boolean release(int arg0) {
		if (arg0 >= this.referenceCount) {
			this.referenceCount = 0;
			return this.message.release();
		} else {
			this.referenceCount -= arg0;
			return false;
		}
	}

	@Override
	public ReferenceCounted retain() {
		return retain(1);
	}

	@Override
	public ReferenceCounted retain(int arg0) {
		this.referenceCount += arg0;
		return this;
	}

}
