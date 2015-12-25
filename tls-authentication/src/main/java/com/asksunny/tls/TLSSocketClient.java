package com.asksunny.tls;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class TLSSocketClient {

	public static final String KEYSTORE_PASS = "changeit";
	public static final String TRUSTSTORE_PASS = "changeit";
	public static final String CLIENT_KEY_ALIAS = "socketclient";

	public TLSSocketClient() {
	}

	public static void main(String[] args) throws Exception {	
		SSLSocketFactory socketFactory = AliasSpecificSSLSocketFactory.getSSLSocketFactory(
				TLSSocketClient.class.getResourceAsStream("/client_keystore.jks"), KEYSTORE_PASS, CLIENT_KEY_ALIAS,
				TLSSocketClient.class.getResourceAsStream("/server_keystore.jks"), TRUSTSTORE_PASS);
		SSLSocket socket = (SSLSocket) socketFactory.createSocket("localhost", 8889);		

		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		PrintWriter writer = new PrintWriter(out);
		String line = null;

		writer.println("Hello Server");
		writer.flush();
		line = br.readLine();
		System.out.printf("From server:%s\n", line);

		writer.println("who am I");
		writer.flush();
		line = br.readLine();
		System.out.printf("From server:%s\n", line);

		writer.println("shutdown");
		writer.flush();
		socket.close();

	}

}
