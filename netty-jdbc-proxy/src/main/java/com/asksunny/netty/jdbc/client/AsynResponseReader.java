package com.asksunny.netty.jdbc.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

public class AsynResponseReader implements Callable<List<String[]>> {

	private DataInputStream in;
	
	public AsynResponseReader(DataInputStream in) {	
		this.in = in;
	}

	@Override
	public List<String[]> call() throws Exception {
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
				for (String[] strings : dataSet) {
					for (int k = 0; k < strings.length; k++) {
						System.out.println(strings[k]);
					}					
				}
				responded = true;
			} catch (SocketTimeoutException scktex) {
				responded = false;
			}
		}
		return dataSet;		
	}

	

}
