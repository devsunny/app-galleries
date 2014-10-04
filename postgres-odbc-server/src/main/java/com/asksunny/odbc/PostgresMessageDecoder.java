package com.asksunny.odbc;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class PostgresMessageDecoder extends ByteToMessageDecoder {

	boolean inited  = false;
	
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if(inited){
			if (in.readableBytes() < 5) {
				return;
			}
			
			in.markReaderIndex();
			int type = in.readByte();
			int length = in.readInt() - 4;
			if (in.readableBytes() < length) {
				in.resetReaderIndex();
				return;
			}
			ByteBuf msg = in.readBytes(length);
			PostgresMessage omsg = new PostgresMessage(type, msg);
			out.add(omsg);	
		}else{
			if (in.readableBytes() < 4 ) {
				return;
			}
			int length = in.readInt() - 4;
			if (in.readableBytes() < length) {
				in.resetReaderIndex();
				return;
			}
			ByteBuf msg = in.readBytes(length);
			PostgresMessage omsg = new PostgresMessage(0, msg);
			out.add(omsg);	
			inited  = true;			
		}
	}

}
