package com.asksunny.ebcdic;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EBCDICInputStreamTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/*
	 * PIC S9(5)
	 */
	@Test
	public void test() throws Exception {
		String hexData = "F0F0F1F2D3";
		EBCDICInputStream cin = new EBCDICInputStream(new ByteArrayInputStream(hexToBinary(hexData)));
		assertEquals("-00123", cin.readPicDecimal(5));
	}

	/*
	 * PIC S9(5)
	 */
	@Test
	public void test2() throws Exception {
		String hexData = "F0F0F1F2C3";
		EBCDICInputStream cin = new EBCDICInputStream(new ByteArrayInputStream(hexToBinary(hexData)));
		assertEquals("00123", cin.readPicDecimal(5));
	}

	
	/*
	 * PIC S9(5)
	 */
	@Test
	public void test3() throws Exception {
		String hexData = "C5848781994040404040D1969585A24040404040001602250C";
		EBCDICInputStream cin = new EBCDICInputStream(new ByteArrayInputStream(hexToBinary(hexData)));
		assertEquals("Edgar     ", cin.readString(10));
		assertEquals("Jones     ", cin.readString(10));
		//assertEquals("Jones     ", cin.readString(10));
	}

	
	
	
	
	
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
