package com.asksunny.tls;

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
/**
 * 
 * <pre>
 * keytool -genkey -keypass "changeit" -dname "CN=Sample Cert, OU=R&D, O=Company Ltd., L=New York City, S=NY, C=US" 
 *         -keyalg RSA -alias myserver -keystore selfsigned.jks -validity 3650  -keysize 2048 -storepass "changeit"
 *         
 * keytool -v -list -keystore selfsigned.jks -storepass "changeit"
 * </pre>
 * 
 * @author SunnyLiu
 *
 */
public class TLSSocketServer {

	public static final String KEYSTORE_PASS = "changeit";
	public static final String TRUSTSTORE_PASS = "changeit";
	public static final String DEFAULT_SSL_PROTOCOL = "TLSv1.2";

	private int port;
	private AtomicBoolean serving = new AtomicBoolean(true);

	public TLSSocketServer(int port) {
		this.port = port;
	}

	public void serve() throws Exception {

		SSLServerSocketFactory sslServerSocketFactory = AliasSpecificSSLSocketFactory.getSSLServerSocketFactory(
				TLSSocketServer.class.getResourceAsStream("/server_keystore.jks"), KEYSTORE_PASS, null,
				TLSSocketServer.class.getResourceAsStream("/client_keystore.jks"), TRUSTSTORE_PASS);
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
			System.out.println(principal);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			PrintWriter writer = new PrintWriter(out);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.trim().equals("bye")) {
					break;
				} else if (line.trim().equals("shutdown")) {
					this.serving.set(false);
					break;
				} else {
					writer.println(line);
					writer.flush();
				}
			}
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
		TLSSocketServer server = new TLSSocketServer(8889);
		server.serve();
	}

}
