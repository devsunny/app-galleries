package com.asksunny.odbc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class NameValuePairEncoder extends MessageToByteEncoder<NameValuePair> {

	public NameValuePairEncoder() {
		this(true);
	}

	public NameValuePairEncoder(boolean preferDirect) {
		super(preferDirect);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, NameValuePair msg,
			ByteBuf out) throws Exception 
	{     
	        out.writeByte((int)'S');
	        int idx = out.writerIndex();
	        out.writeInt(0);
	        out.writeBytes(msg.getName().getBytes(CharsetUtil.US_ASCII));
	        out.writeByte(0);
	        out.writeBytes(msg.getValue().getBytes(CharsetUtil.US_ASCII));
	        out.writeByte(0);
	        int len = out.readableBytes()-1;
	        out.setInt(idx, len);
	}

}
