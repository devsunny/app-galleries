package com.asksunny.netty.jdbc;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;

import com.asksunny.netty.jdbc.test.StressTestDataSet;

public class SimpleJdbcProxyHandler extends SimpleChannelInboundHandler<String>
{

	private String handlerId = UUID.randomUUID().toString();
	public static final int MB = 1024 *1024;
	
	
	@Override
	protected void channelRead0(
			ChannelHandlerContext channelCtx, String jdbcCommand)
			throws Exception 
	{			
		ByteBuf buf = channelCtx.alloc().buffer(1024);		
		ByteBufOutputStream bout = new ByteBufOutputStream(buf);
		if(jdbcCommand.equals("Q")){
			StressTestDataSet.compressMetaBase(bout, UUID.randomUUID().toString(), 1, 2);			
		}else if(jdbcCommand.startsWith("R")){
			String cntstr = jdbcCommand.length()>1?jdbcCommand.substring(1):"250";
			int cnt = Integer.valueOf(cntstr);
			System.out.println("Request:" + cnt);
			StressTestDataSet.compressData(cnt, bout, UUID.randomUUID().toString(), 1, 2);
		}		
		channelCtx.writeAndFlush(buf);			
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
		cause.printStackTrace();
		ctx.close();
	}
	
	

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception 
	{
		
	}

	
	
}
