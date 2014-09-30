package com.asksunny.odbc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PostgresMessageEncoder extends
		MessageToByteEncoder<PostgresMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, PostgresMessage msg,
			ByteBuf out) throws Exception {
		
		
	}

}
