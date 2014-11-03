package com.asksunny.ebcdic;

/*
 01 INPUT-DATA-REC.
 05  FIRST-NAME			PIC X(10).
 05  LAST-NAME			PIC X(10).
 05  AGE			PIC S9(4) COMP.
 05  HOURLY-RATE 			PIC S9(3)V9(2) COMP-3.

 */

/*
 First Name: Edgar
 Last Name: Jones
 Age: 22
 Hourly Rate: 22.50
 */

public class EbcdicUtil {

	String data = "C5848781994040404040D1969585A24040404040001602250C";

	private static byte[] hexToBinary(String data) {
		byte[] buffer = new byte[data.length() / 2];
		for (int i = 0; i < buffer.length; ++i) {
			int msb = hex.indexOf(data.charAt(i * 2));
			int lsb = hex.indexOf(data.charAt(i * 2 + 1));
			buffer[i] = (byte) (msb << 4 | lsb);
		}
		return (buffer);
	}

	private static final String hex = "0123456789ABCDEF";

}
