package com.asksunny.kmip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import com.asksunny.tls.AliasSpecificSSLSocketFactory;

public class SimpleKmipServer {

	public static final String KEYSTORE_PASS = "changeit";
	public static final String TRUSTSTORE_PASS = "changeit";
	public static final String DEFAULT_SSL_PROTOCOL = "TLSv1.2";

	private int port = 5696;
	private AtomicBoolean serving = new AtomicBoolean(true);

	public SimpleKmipServer(int port) {
		this.port = port;
	}
	
	public SimpleKmipServer() {
		this.port = 5696;
	}

	public void serve() throws Exception {

		SSLServerSocketFactory sslServerSocketFactory = AliasSpecificSSLSocketFactory.getSSLServerSocketFactory(
				SimpleKmipServer.class.getResourceAsStream("/server_keystore.jks"), KEYSTORE_PASS, null,
				SimpleKmipServer.class.getResourceAsStream("/client_keystore.jks"), TRUSTSTORE_PASS);
		SSLServerSocket serverSock = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
		serverSock.setNeedClientAuth(true);
		while (serving.get()) {
			SSLSocket socket = (SSLSocket) serverSock.accept();
			socket.setEnableSessionCreation(true);
			handleConnection(socket);
		}

	}

	protected void handleConnection(SSLSocket socket) {
		try {
			String  principal = getPrincipal(socket);
			//detected the protocok
			//HTTP starts with POST, GET, PUT, DELETE etc
			//KMIP start with 4200 first two bytes 
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	protected String getPrincipal(SSLSocket socket) throws SSLPeerUnverifiedException
	{
		String principal = null;
		Certificate[] certs = socket.getSession().getPeerCertificates();
		for (int i = 0; i < certs.length; i++) {				
			if(certs[i] instanceof X509Certificate){
				principal = ((X509Certificate)certs[i]).getIssuerDN().getName();
				break;
			}			
		}
		return principal;
	}

	public static void main(String[] args) throws Exception {
		SimpleKmipServer server = new SimpleKmipServer(8889);
		server.serve();
	}

}
