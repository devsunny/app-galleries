package com.asksunny.odbc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class SSLNegotiateDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception 
	{
		if (in.readableBytes() < 4) {
			return;
		}
		in.markReaderIndex();		
		int length = in.readInt() - 4;
			
		if (in.readableBytes() < length ) {
			in.resetReaderIndex();
			return;
		}
		ByteBuf msg = in.readBytes(length);
		out.add(msg);
		
	}

	

}
