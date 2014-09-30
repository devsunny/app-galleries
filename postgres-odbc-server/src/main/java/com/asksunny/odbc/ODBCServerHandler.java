package com.asksunny.odbc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import java.util.List;

public class ODBCServerHandler extends ByteToMessageDecoder {

	private SslContext sslCtx = null;

	public ODBCServerHandler() {
		super();
	}

	public ODBCServerHandler(SslContext sslCtx) {
		super();
		this.sslCtx = sslCtx;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (in.readableBytes() < 5) {
			return;
		}
		if (SslHandler.isEncrypted(in)) {
			switchToJdbcServer(ctx);
		} else {
			switchToSSLNegotiate(ctx);
		}

	}
	
	
	private void switchToJdbcServer(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();
        System.out.println("World1");
        p.addLast("ssl", sslCtx.newHandler(ctx.alloc()));
        p.addLast("postgres-message-decoder", new PostgresMessageDecoder());
        p.addLast("postgres-message-encoder", new PostgresMessageEncoder());   
        p.addLast("jdbcServerHandler", new JDBCServerHandler());
        p.remove(this);
    }
	
	private void switchToSSLNegotiate(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();
        System.out.println("Hello1");
        p.addLast("ssl-negodecoder", new SSLNegotiateDecoder());       
        p.addLast("bytearray-encoder", new ByteArrayEncoder());
        p.addLast("ssl-NegoHandler", new SSLNegotiateHandler());        
        p.remove(this);
    }
	
	

	public SslContext getSslCtx() 
	{
		return sslCtx;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
