package com.asksunny.netty.jdbc.client;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class JdbcPlainSocketClient {

	public void testServer() throws Exception {
		int i = 0;
		while (i < 1) {
			SSLContext sc = SSLContext.getInstance("TLSv1");
			sc.init(null,
					InsecureTrustManagerFactory.INSTANCE.getTrustManagers(),
					new java.security.SecureRandom());
			SSLSocketFactory sf = sc.getSocketFactory();
			SSLSocket clientSocket = (SSLSocket) sf.createSocket("localhost",
					20443);
			DataInputStream in = new DataInputStream(
					clientSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(
					clientSocket.getOutputStream());
			for (int j = 0; j < 1; j++) {
				byte[] test = "HELLO".getBytes(StandardCharsets.UTF_8);
				int tlen = test.length;
				out.writeInt(tlen);
				out.write(test, 0, test.length);
				out.flush();
				boolean responded = false;
				while (!responded) {
					try {
						int plen = in.readInt();
						byte[] tmp = new byte[plen];
						in.readFully(tmp);
						GZIPInputStream gin = new GZIPInputStream(
								new ByteArrayInputStream(tmp));
						int b = gin.read();						
						ByteArrayOutputStream bou = new ByteArrayOutputStream();	
						ArrayList<String[]> dataSet = new ArrayList<>();
						ArrayList<String> row = new ArrayList<>();
						while (b != -1) {
							if(b==1){
								String lastCol = new String(bou.toByteArray(), StandardCharsets.UTF_8);
								row.add(lastCol);
								String[] strRow = new String[row.size()]; 
								strRow = row.toArray(strRow);
								dataSet.add(strRow);
								row.clear();
								bou = new ByteArrayOutputStream();
							}else if(b==2){
								String col = new String(bou.toByteArray(), StandardCharsets.UTF_8);
								row.add(col);								
								bou = new ByteArrayOutputStream();
							}else{
								bou.write(b);
							}
							
							b = gin.read();
							if(b==-1){
								String lastCol = new String(bou.toByteArray(), StandardCharsets.UTF_8);
								row.add(lastCol);
								String[] strRow = new String[row.size()]; 
								strRow = row.toArray(strRow);
								dataSet.add(strRow);								
							}
						}						
						for (String[] strings : dataSet) {
							for (int k = 0; k < strings.length; k++) {
								System.out.println(strings[k]);
							}
							System.out.println("************************");
						}
						responded = true;
					} catch (SocketTimeoutException scktex) {
						responded = false;
					}
				}

			}
			clientSocket.close();
			i++;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		for (int i = 0; i < 1; i++) {
			Thread t = new Thread(new Runnable() {//
						@Override
						public void run() {
							try {
								JdbcPlainSocketClient client = new JdbcPlainSocketClient();
								client.testServer();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, "T" + i);
			t.start();
		}
	}

}
