package com.asksunny.netty.jdbc.client;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

public class SimpleJdbcClientHandler extends SimpleChannelInboundHandler<String>
{

	private String handlerId = UUID.randomUUID().toString();
	
	
	
	
	@Override
	protected void channelRead0(
			ChannelHandlerContext channelCtx, String jdbcCommand)
			throws Exception 
	{
		
		
		
		
	}
	
	
	
	

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception 
	{		
		super.userEventTriggered(ctx, evt);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception 
	{
		
	}

	
	
}
