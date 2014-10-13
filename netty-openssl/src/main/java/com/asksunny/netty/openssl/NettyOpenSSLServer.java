package com.asksunny.netty.openssl;

import java.io.File;

import io.netty.handler.ssl.SslProvider;

import io.netty.handler.ssl.SslContext;



public class NettyOpenSSLServer {

	public NettyOpenSSLServer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		  SslContext sslCtx = SslContext.newServerContext(SslProvider.OPENSSL, new File("Path_to_Pem"), new File("path_to_key"), "password");
		
	}
}
