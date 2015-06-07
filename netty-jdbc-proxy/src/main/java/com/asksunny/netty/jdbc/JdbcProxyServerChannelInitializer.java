package com.asksunny.netty.jdbc;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

import java.nio.charset.StandardCharsets;

public class JdbcProxyServerChannelInitializer extends
		ChannelInitializer<SocketChannel> {

	private final SslContext sslContex;
	public static final int MAX_FRAME_SIZE = 1024 * 1024 * 4;
	public static final int INT_LENGTH = 4;

	public JdbcProxyServerChannelInitializer(SslContext sslCtx) {
		this.sslContex = sslCtx;
	}

	@Override
	protected void initChannel(SocketChannel sc) throws Exception {
		ChannelPipeline pipeline = sc.pipeline();
		if (this.sslContex != null) {
			pipeline.addLast(sslContex.newHandler(sc.alloc()));
		}
		pipeline.addLast("frameDecoder", new JdbcCommandDecoder(MAX_FRAME_SIZE,
				0, INT_LENGTH, 0, INT_LENGTH));
		pipeline.addLast("stringDecoder", new StringDecoder(
				StandardCharsets.UTF_8));
		pipeline.addLast("frameEncoder", new LengthFieldPrepender(INT_LENGTH));
		pipeline.addLast("stringEncoder", new StringEncoder(
				StandardCharsets.UTF_8));
		pipeline.addLast("jdbcProxyHandler", new SimpleJdbcProxyHandler());

	}

}
