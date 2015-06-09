package com.asksunny.netty.jdbc.client;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class JdbcPlainSocketClient {

	
	ExecutorService executor = Executors.newSingleThreadExecutor();
	
	
	public void testServer() throws Exception {
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, InsecureTrustManagerFactory.INSTANCE.getTrustManagers(),
				new java.security.SecureRandom());
		SSLSocketFactory sf = sc.getSocketFactory();
		SSLSocket clientSocket = (SSLSocket) sf
				.createSocket("localhost", 20443);
		DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		DataOutputStream out = new DataOutputStream(
				clientSocket.getOutputStream());
		byte[] test = "Q".getBytes(StandardCharsets.UTF_8);
		int tlen = test.length;
		out.writeInt(tlen);
		out.write(test, 0, test.length);
		out.flush();
		readResponse(in);
		byte[] R = "R300000".getBytes(StandardCharsets.UTF_8);
		int dlen = R.length;
		out.writeInt(dlen);
		out.write(R, 0, R.length);
		out.flush();
		System.out.println("Here");
		Future<List<String[]>> future = executor.submit(new AsynResponseReader(in));		
		while(!future.isDone()){
			Thread.sleep(100);
		}
		List<String[]> response = future.get();
		for (String[] strings : response) {
			for (int k = 0; k < strings.length; k++) {
				System.out.println(strings[k]);
			}					
		}
		clientSocket.close();
	}
	
	
	
	
	
	
	protected List<String[]> readResponse(DataInputStream in)
			throws IOException {
		ArrayList<String[]> dataSet = new ArrayList<>();
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

				ArrayList<String> row = new ArrayList<>();
				while (b != -1) {
					if (b == 1) {
						String lastCol = new String(bou.toByteArray(),
								StandardCharsets.UTF_8);
						row.add(lastCol);
						String[] strRow = new String[row.size()];
						strRow = row.toArray(strRow);
						dataSet.add(strRow);
						row.clear();
						bou = new ByteArrayOutputStream();
					} else if (b == 2) {
						String col = new String(bou.toByteArray(),
								StandardCharsets.UTF_8);
						row.add(col);
						bou = new ByteArrayOutputStream();
					} else {
						bou.write(b);
					}

					b = gin.read();
					if (b == -1) {
						String lastCol = new String(bou.toByteArray(),
								StandardCharsets.UTF_8);
						row.add(lastCol);
						String[] strRow = new String[row.size()];
						strRow = row.toArray(strRow);
						dataSet.add(strRow);
					}
				}				
				responded = true;
			} catch (SocketTimeoutException scktex) {
				responded = false;
			}
		}
		return dataSet;
	}

	public static void renderRequest(PrintWriter pw, int fieldDelimiter,
			int sfDelimiter, int fcode, int sfcode, Object... cmds) {

		pw.print((char) fcode);
		if (sfcode > 0) {
			pw.print((char) sfcode);
		}
		int clen = cmds.length;
		for (int i = 0; i < clen; i++) {
			Object obj = cmds[i];
			if (obj == null) {
				;
			} else if (obj.getClass().isArray()) {
				Class<?> ofArray = obj.getClass().getComponentType();
				if (ofArray.isPrimitive()) {
					int length = Array.getLength(obj);
					for (int j = 0; j < length; j++) {
						Object aobj = Array.get(obj, j);
						if (aobj != null) {
							pw.print(aobj.toString());
						}
						if (j < length - 1) {
							pw.print((char) sfDelimiter);
						}
					}
				} else {
					Object[] objarray = (Object[]) obj;
					for (int j = 0; j < objarray.length; j++) {
						if (objarray[j] != null) {
							pw.print(objarray[j].toString());
						}
						if (j < objarray.length - 1) {
							pw.print((char) sfDelimiter);
						}
					}
				}
			} else {
				pw.print(obj.toString());
			}
			pw.print((char) fieldDelimiter);

		}
		pw.flush();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		JdbcPlainSocketClient client = new JdbcPlainSocketClient();
		client.testServer();
	}

}
