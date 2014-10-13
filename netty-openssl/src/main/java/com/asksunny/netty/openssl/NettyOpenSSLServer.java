package com.asksunny.netty.openssl;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslProvider;

import java.io.File;

public class NettyOpenSSLServer {

	public NettyOpenSSLServer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		SslContext sslCtx = SslContext.newServerContext(SslProvider.OPENSSL,
				new File("D:/temp/server.crt"), new File("D:/temp/server_private_key.PEM"), null);
		SocketChannel ch = null; //fix here;
		sslCtx.newHandler(ch.alloc());
	}
}
