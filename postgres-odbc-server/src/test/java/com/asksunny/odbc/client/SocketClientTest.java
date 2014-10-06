package com.asksunny.odbc.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class SocketClientTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		Socket client = new Socket("localhost", 5432);
		client.setTcpNoDelay(true);		
		DataOutputStream out = new DataOutputStream(client.getOutputStream());
		DataInputStream in = new DataInputStream(client.getInputStream());
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
