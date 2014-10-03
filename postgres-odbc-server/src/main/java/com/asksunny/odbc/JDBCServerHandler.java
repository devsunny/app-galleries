package com.asksunny.odbc;

import java.util.Properties;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class JDBCServerHandler extends SimpleChannelInboundHandler<PostgresMessage>  
{

	private Properties connectionInfo;

	public JDBCServerHandler() {
		connectionInfo = new Properties();
	}

	

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PostgresMessage postgresMessage)
			throws Exception 
	{
		PostgresMessage resp = new PostgresMessage();		
		if(postgresMessage.getMessageType()==0){			
			ByteBuf in = postgresMessage.getMessage();
			int val = in.readInt();
			//byte[]  keyValPairs = in.array();
			int len = in.readableBytes();
			byte[] data = new byte[len];
			in.readBytes(data);
			int start = 0;
			String key = null;
			for(int i=0; i<data.length; i++)
			{
				byte b = data[i];
				if(b==0 || i==data.length-1){
					if(i==start) break;
					String strVal = new String(data, start, i-start);					
					if(key==null){
						key = strVal;
					}else{
						connectionInfo.setProperty(key, strVal);
						key = null;
					}
					start = i +1;
				}
				
			}
			resp.setMessageType('R');			
		    ByteBuf buf = Unpooled.buffer();
		    buf.writeInt(3);
		    resp.setMessage(buf);
		    ctx.writeAndFlush(resp);		    
		}else{
			System.out.println("What Type:" + (char)postgresMessage.getMessageType());
			switch(postgresMessage.getMessageType())
			{
			case 'p':
				String password = postgresMessage.getMessage().toString(CharsetUtil.US_ASCII);
				connectionInfo.setProperty("password", password);
				//Authentication Here;
				break;
			case 'P':
				//prepared
				break;
			}
			
		}
		
		
		
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



	public Properties getConnectionInfo() {
		return connectionInfo;
	}



	public void setConnectionInfo(Properties connectionInfo) {
		this.connectionInfo = connectionInfo;		
	}
	
	
	

	

}
