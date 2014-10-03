package com.asksunny.odbc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SSLNegotiateHandler extends SimpleChannelInboundHandler<Integer> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Integer msg)
			throws Exception {
		ctx.writeAndFlush("S");	
		ctx.pipeline().addFirst(new ODBCServerHandler());
		ctx.pipeline().remove(this);
		ctx.pipeline().remove("ssl-negodecoder");
		ctx.pipeline().remove("String-encoder");			
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
