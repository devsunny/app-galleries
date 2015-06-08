package com.asksunny.netty.jdbc.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.GregorianCalendar;
import java.util.zip.GZIPOutputStream;

public class StressTestDataSet {

	public static int columnCount = 12;
	public static String[] columnNames = new String[] { "ID", "First_NAME",
			"MIDDLE_NAME", "LAST_NAME", "DOB", "SSN", "HOUSE_NO", "STREET",
			"CITY", "STATE", "ZIP_CODE", "CREATED_TIME" };
	public static int[] columnTypes = new int[] { Types.INTEGER, Types.VARCHAR,
			Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.VARCHAR,
			Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
			Types.VARCHAR, Types.TIMESTAMP };
	public static int[] displaySizes = new int[] { 16, 32, 32, 32, 8, 16, 32,
			128, 64, 32, 5, 8 };

	public static final Object[] DATAROW = new Object[] { 1, "John", "Bogus", "Doe",
			new GregorianCalendar(1983, 10, 21).getTime(), "123-45-6789",
			"123", "Main Street", "New York", "NY", "12345",
			new GregorianCalendar(2010, 5, 21, 9, 30, 21).getTime() };

	public static void compressMetaBase(OutputStream out, String rsId,
			int rowDelimiter, int coldelimiter) throws SQLException,
			IOException {
		GZIPOutputStream zout = new GZIPOutputStream(out);
		PrintWriter pw = new PrintWriter(zout);
		serializeMetaBase(pw, rsId, (char) rowDelimiter, (char) coldelimiter);
		pw.flush();
		zout.flush();
		zout.close();
	}

	public static void compressData(int numRows, OutputStream out, String rsId,
			int rowDelimiter, int coldelimiter) throws SQLException,
			IOException {
		GZIPOutputStream zout = new GZIPOutputStream(out);
		PrintWriter pw = new PrintWriter(zout);
		serializeData(numRows, pw, rsId, (char) rowDelimiter,
				(char) coldelimiter);
		pw.flush();
		zout.flush();
		zout.close();
	}
	
	

	public static void serializeData(int numRows, PrintWriter pw, String rsId,
			char rowDelimiter, char coldelimiter) throws SQLException {

		for (int i = 0; i < numRows; i++) {
			DATAROW[0] = i+1; 
			for (int j = 0; j < DATAROW.length; j++) 
			{
				pw.print(DATAROW[j].toString());
				if (j < DATAROW.length - 1) {
					pw.print(coldelimiter);
				}
			}
			if(i<numRows-1){
				pw.print(rowDelimiter);
			}
		}
		pw.flush();

	}

	public static void serializeMetaBase(PrintWriter pw, String rsId,
			char rowDelimiter, char coldelimiter) throws SQLException {
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
			if (colIdx < colCount - 1) {
				pw.print(coldelimiter);
			}
		}
		pw.print(rowDelimiter);
		for (colIdx = 0; colIdx < colCount; colIdx++) {
			pw.print(StressTestDataSet.columnNames[colIdx]);
			if (colIdx < colCount - 1) {
				pw.print(coldelimiter);
			}
		}
		pw.print(rowDelimiter);
		for (colIdx = 0; colIdx < colCount; colIdx++) {
			pw.print(StressTestDataSet.displaySizes[colIdx]);
			if (colIdx < colCount - 1) {
				pw.print(coldelimiter);
			}
		}
	}

}
