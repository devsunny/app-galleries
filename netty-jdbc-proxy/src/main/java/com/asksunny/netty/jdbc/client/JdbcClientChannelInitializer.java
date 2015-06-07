package com.asksunny.netty.jdbc.client;

import java.nio.charset.StandardCharsets;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class JdbcClientChannelInitializer extends
		ChannelInitializer<SocketChannel> {

	private final SslContext sslContex;
	public static final int MAX_FRAME_SIZE = 1024 * 1024 * 4;

	public JdbcClientChannelInitializer(SslContext sslCtx) {
		this.sslContex = sslCtx;
	}

	@Override
	protected void initChannel(SocketChannel sc) throws Exception {
		ChannelPipeline pipeline = sc.pipeline();
		if (this.sslContex != null) {
			pipeline.addLast(sslContex.newHandler(sc.alloc()));
		}
		pipeline.addLast("timeout", new ReadTimeoutHandler(60));
		pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(MAX_FRAME_SIZE, 0, 4, 0, 4));
		pipeline.addLast("stringDecoder", new StringDecoder(StandardCharsets.UTF_8));
		
		pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
		pipeline.addLast("stringEncoder", new StringEncoder(StandardCharsets.UTF_8));	
		pipeline.addLast("jdbcProxyHandler", new SimpleJdbcClientHandler());

	}

}
