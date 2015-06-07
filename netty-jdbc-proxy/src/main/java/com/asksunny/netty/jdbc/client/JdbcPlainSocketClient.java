package com.asksunny.netty.jdbc.client;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class JdbcPlainSocketClient {

	
	
	
	public void testServer() throws Exception
	{
		int i=0;
		while(i < 1000){		
			SSLContext sc = SSLContext.getInstance("TLS"); 
		    sc.init(null, InsecureTrustManagerFactory.INSTANCE.getTrustManagers(), new java.security.SecureRandom()); 	    
			SSLSocketFactory sf = sc.getSocketFactory();	    
			SSLSocket clientSocket = (SSLSocket) sf.createSocket("localhost", 20443);
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());		 
			
			for(int j=0; j<12; j++){
				byte[] test = "HELLO".getBytes(StandardCharsets.UTF_8);		
				int tlen = test.length;		
				out.writeInt(tlen);
				out.write(test, 0, test.length);
				out.flush();		
				int plen = in.readInt();				
				byte[]  tmp = new byte[plen];		
				in.readFully(tmp);
				GZIPInputStream gin = new GZIPInputStream(new ByteArrayInputStream(tmp));		
				int b = gin.read();		
				StringWriter sw = new StringWriter();
				while(b!=-1){			
					if(b!=65){
						System.out.println("Hello");
					}else{
						sw.write(b);
					}
					b = gin.read();
				}		
				
			}
			clientSocket.close();
			i++;
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		
		for(int i=0; i<20; i++){
			Thread t = new Thread(new Runnable() {
				
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
