package com.asksunny.odbc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class JDBCServerHandler extends SimpleChannelInboundHandler<PostgresMessage>  
{

	public JDBCServerHandler() {
	
	}

	

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PostgresMessage postgresMessage)
			throws Exception 
	{
	
		
	}



	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {	
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {		
		super.handlerRemoved(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {		
		super.channelUnregistered(ctx);
	}
	
	
	

	

}
