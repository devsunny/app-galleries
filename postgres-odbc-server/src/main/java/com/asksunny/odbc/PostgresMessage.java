package com.asksunny.odbc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
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

	public ByteBuf createBuffer() {
		if (this.message == null) {
			this.message = Unpooled.buffer();
		}
		return this.message;
	}
	
	public PostgresMessage initMessage() {
		if (this.message == null) {
			this.message = Unpooled.buffer();
		}
		return this;
	}

	public String readString() {
		byte[] buf = new byte[this.message.readableBytes()];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = this.message.readByte();
			if (buf[i] == 0) {
				break;
			}
		}
		return new String(buf, CharsetUtil.US_ASCII);
	}

	public PostgresMessage writeString(String data) {
		this.message.writeBytes(data.getBytes(CharsetUtil.US_ASCII)).writeByte(
				0);
		return this;
	}

	public PostgresMessage writeString(String data1, String data2) {
		this.message.writeBytes(data1.getBytes(CharsetUtil.US_ASCII))
				.writeBytes(data2.getBytes(CharsetUtil.US_ASCII)).writeByte(0);
		return this;
	}

	public PostgresMessage writeString(String data1, String data2, String data3) {
		this.message.writeBytes(data1.getBytes(CharsetUtil.US_ASCII))
				.writeBytes(data2.getBytes(CharsetUtil.US_ASCII))
				.writeBytes(data3.getBytes(CharsetUtil.US_ASCII)).writeByte(0);
		return this;
	}
	
	public PostgresMessage writeString(String data1, String data2, String data3, String... moreStrs) {
		this.message.writeBytes(data1.getBytes(CharsetUtil.US_ASCII))
				.writeBytes(data2.getBytes(CharsetUtil.US_ASCII))
				.writeBytes(data3.getBytes(CharsetUtil.US_ASCII));
		for(String x:moreStrs){
			this.message.writeBytes(x.getBytes(CharsetUtil.US_ASCII));
		}
		this.message.writeByte(0);
		return this;
	}
	
	public PostgresMessage writeByte(int byteVal) {		
		this.message.writeByte(byteVal);
		return this;
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
	
	public PostgresMessage(int messageType) {
		this(messageType, null);		
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
