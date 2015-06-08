package com.asksunny.netty.jdbc;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.asksunny.netty.jdbc.test.StressTestDataSet;

public class ResultSetSerDeTest {

	String data = null;
	byte[] binaryData = null;
	
	@Before
	public void setUp() throws Exception {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		serializeMetaBase( pw, UUID.randomUUID().toString(), (char)1, (char)2);
		pw.flush();
		data = sw.toString();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		GZIPOutputStream gout = new GZIPOutputStream(bout);
		gout.write(data.getBytes(StandardCharsets.UTF_8));
		gout.flush();
		gout.close();
		binaryData = bout.toByteArray();
		ByteArrayOutputStream bout2 = new ByteArrayOutputStream();			
		DataOutputStream out = new DataOutputStream(bout2);		
		out.writeInt(binaryData.length);
		out.write(binaryData);
		out.flush();
		out.close();
		binaryData = bout2.toByteArray();
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void test()  throws Exception
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(binaryData);
		DataInputStream din = new DataInputStream(bin);
		List<String[]> data = ResultSetSerDe.readCompress(din, 1, 2);
		for (String[] strings : data) {
			for (int i = 0; i < strings.length; i++) {
				System.out.println(strings[i]);
			}
			System.out.println("*********");
		}
		assertEquals(4, data.size());
		assertEquals(7, data.get(0).length);
		assertEquals(12, data.get(3).length);		
		assertEquals("512", data.get(0)[1]);
		assertEquals("LAST_NAME", data.get(2)[3]);
		
	}
	
	
	
	public static void serializeMetaBase(PrintWriter pw, String rsId, char rowDelimiter, char coldelimiter)
			throws SQLException {
		pw.print(rsId);
		pw.print(coldelimiter);
		pw.print(512);
		pw.print(coldelimiter);
		pw.print(ResultSet.FETCH_FORWARD);
		pw.print(coldelimiter);
		pw.print(ResultSet.CONCUR_READ_ONLY);
		pw.print(coldelimiter);
		pw.print(ResultSet.HOLD_CURSORS_OVER_COMMIT);
		pw.print(coldelimiter);
		pw.print("Test");
		pw.print(coldelimiter);	
		int colCount = StressTestDataSet.columnCount;
		pw.print(colCount);
		pw.print(rowDelimiter);
		int colIdx = 0;
		int[] types = new int[colCount];
		for (colIdx = 0; colIdx < colCount; colIdx++) {
			types[colIdx] = StressTestDataSet.columnTypes[colIdx];
			pw.print(types[colIdx]);
			if (colIdx < colCount) {
				pw.print(coldelimiter);
			}
		}
		pw.print(rowDelimiter);
		for (colIdx = 0; colIdx < colCount; colIdx++) {
			pw.print(StressTestDataSet.columnNames[colIdx]);
			if (colIdx < colCount) {
				pw.print(coldelimiter);
			}
		}
		pw.print(rowDelimiter);
		for (colIdx = 0; colIdx < colCount; colIdx++) {
			pw.print(StressTestDataSet.displaySizes[colIdx]);
			if (colIdx < colCount) {
				pw.print(coldelimiter);
			}
		}		
	}

}
