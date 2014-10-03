package com.asksunny.odbc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import java.util.List;

import javax.net.ssl.SSLEngine;

import com.asksunny.ssl.SecureSocketSslContextFactory;

public class ODBCServerHandler extends ByteToMessageDecoder {

	

	public ODBCServerHandler() {
		super();
	}
	

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (SslHandler.isEncrypted(in)) {
			switchToSSLServer(ctx);
		} else {
			switchToSSLNegotiate(ctx);
		}

	}

	private void switchToSSLServer(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.pipeline();
		SSLEngine engine = SecureSocketSslContextFactory.getServerContext()
				.createSSLEngine();
		engine.setUseClientMode(false);
		p.addLast("ssl", new SslHandler(engine));
		p.addLast("postgres-message-decoder", new PostgresMessageDecoder());
		p.addLast("postgres-message-encoder", new PostgresMessageEncoder());
		p.addLast("jdbcServerHandler", new JDBCServerHandler());
		p.remove(this);
	}

	private void switchToSSLNegotiate(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.pipeline();
		p.addLast("ssl-negodecoder", new SSLNegotiateDecoder());
		p.addLast("String-encoder", new SSLNegotiateEncoder());
		p.addLast("ssl-NegoHandler", new SSLNegotiateHandler());
		p.remove(this);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
