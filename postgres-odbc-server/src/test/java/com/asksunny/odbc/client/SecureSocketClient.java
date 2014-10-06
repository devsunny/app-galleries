package com.asksunny.odbc.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.net.ssl.SSLContext;

import com.asksunny.ssl.SecureSocketSslContextFactory;

public class SecureSocketClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Socket clientSocket;
		OutputStream clientOut;
		InputStream clientIn;
		SSLContext clientSslContext = SecureSocketSslContextFactory.getClientContext();
		clientSocket = clientSslContext.getSocketFactory().createSocket("localhost", 5432);
		clientOut = clientSocket.getOutputStream();
		clientIn = clientSocket.getInputStream();
		DataOutputStream out = new DataOutputStream(clientOut);
		DataInputStream in = new DataInputStream(clientIn);		
		out.writeInt(8);
		out.writeInt(80877103);
		out.flush();
		System.out.println("--------------------------------");
		int c = in.readByte();
		System.out.println("--------------------------------");
		System.out.println((char)c);
		in.close();
		out.close();

	}

}
