package com.asksunny.netty.jdbc.test;

import java.sql.Types;
import java.util.GregorianCalendar;

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
	
	public static Object[] row = new Object[] { 1, "John", "Bogus", "Doe",
			new GregorianCalendar(1983, 10, 21).getTime(), "123-45-6789",
			"123", "Main Street", "New York", "NY", "12345",
			new GregorianCalendar(2010, 5, 21, 9, 30, 21).getTime() };

}
