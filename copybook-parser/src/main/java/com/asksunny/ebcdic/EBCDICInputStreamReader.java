package com.asksunny.ebcdic;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EBCDICInputStreamReader extends InputStream {

	private final static int MINUS = 13; // Hex value D;

	private InputStream dataStream = null;
	private ByteOrder endian = ByteOrder.BIG_ENDIAN;

	public EBCDICInputStreamReader(InputStream source) {
		this(source, ByteOrder.BIG_ENDIAN);
	}

	public EBCDICInputStreamReader(InputStream source, ByteOrder endian) {
		this.dataStream = source;
		this.endian = endian;
	}

	public String readString(int length) throws IOException {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int byteval = read();
			buf.append(EBCDIC_CHARSET[byteval]);
		}
		return buf.toString();
	}

	public int readSingedComp(int length) throws IOException {
		int dlen = length > 9 ? 8 : (length < 5 ? 2 : 4);
		ByteBuffer buf = ByteBuffer.allocate(dlen);
		for (int i = 0; i < dlen; i++) {
			buf.put((byte) read());
		}
		buf.flip();
		// Fix here
		return 0;
	}

	public String readComp3(int length) throws IOException {
		return readComp3(length, 0);
	}

	public String readComp3(int length, int decimalPlace) throws IOException {
		length = length + 1;
		int dlen = (length / 2) + (length % 2);
		StringBuilder buf = new StringBuilder();
		boolean negative = false;
		for (int i = 0; i < dlen; i++) {
			int byteval = read();
			int nb1 = (byteval >>> 4) & 0x0F;
			int nb2 = 0x0F & byteval;
			if (nb1 <= 9 && nb1 >= 0) {
				buf.append(nb1);
			} else {
				throw new IOException("Invliad COMP-3 encoding");
			}
			if (i == dlen - 1) {
				if (nb2 == MINUS) {
					negative = true;
				}
			} else {
				if (nb2 <= 9 && nb2 >= 0) {
					buf.append(nb2);
				} else {
					throw new IOException("Invliad COMP-3 encoding");
				}
			}
		}
		if (decimalPlace > 0) {
			buf.insert(decimalPlace, '.');
		}
		if (negative) {
			buf.insert(0, '-');
		}
		return buf.toString();
	}

	public String readPicDecimal(int length) throws IOException {
		return readPicDecimal(length, 0);
	}

	public String readPicDecimal(int length, int decimalPlace)
			throws IOException {
		StringBuilder buf = new StringBuilder();
		boolean negative = false;
		for (int i = 0; i < length; i++) {
			int byteval = read();
			if (i == length - 1) {
				int signNyble = byteval >>> 4;
				int nb2 = 0xF0 | byteval;
				buf.append(EBCDIC_CHARSET[nb2]);
				if (signNyble == MINUS) {
					negative = true;
				}
			} else {
				buf.append(EBCDIC_CHARSET[byteval]);
			}
		}
		if (decimalPlace > 0) {
			buf.insert(decimalPlace, '.');
		}
		if (negative) {
			buf.insert(0, '-');
		}
		return buf.toString();
	}

	@Override
	public int read() throws IOException {
		return dataStream.read();
	}

	@Override
	public void close() throws IOException {
		this.dataStream.close();
	}

	public ByteOrder getEndian() {
		return endian;
	}

	public void setEndian(ByteOrder endian) {
		this.endian = endian;
	}

	protected char[] EBCDIC_CHARSET = new char[] { (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
			(char) -1, (char) -1, (char) -1, (char) -2, (char) -1, (char) -1,
			(char) -1, (char) -1, ' ', (char) -2, (char) -1, (char) -1,
			(char) -2, (char) -1, (char) -1, (char) -1, (char) -2, (char) -2,
			(char) -1, (char) -2, (char) -1, (char) -1, (char) -1, (char) -1,
			(char) -2, (char) -2, (char) -2, (char) -1, (char) -1, (char) -1,
			(char) -2, (char) -1, (char) -1, (char) -2, (char) -2, (char) -2,
			(char) -2, (char) -2, (char) -2, (char) -2, (char) -2, (char) -2,
			'¢', '.', '<', '(', '+', '|', (char) -2, (char) -2, (char) -2,
			(char) -2, (char) -2, (char) -2, (char) -2, (char) -2, (char) -2,
			(char) -2, '!', '$', '*', ')', ';', '¬', '-', '/', (char) -2,
			(char) -2, (char) -2, (char) -2, (char) -2, (char) -2, (char) -2,
			(char) -2, '¦', ',', '%', '_', '>', '?', (char) -2, (char) -2,
			(char) -2, (char) -2, (char) -2, (char) -2, (char) -2, (char) -2,
			(char) -2, '`', ':', '#', '@', '\'', '=', '"', (char) -2, 'a', 'b',
			'c', 'd', 'e', 'f', 'g', 'h', 'i', (char) -2, (char) -2, (char) -2,
			(char) -2, (char) -2, (char) -2, (char) -2, 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', (char) -2, (char) -2, (char) -2,
			(char) -2, (char) -2, (char) -2, (char) -2, '~', 's', 't', 'u',
			'v', 'w', 'x', 'y', 'z', (char) -2, (char) -2, (char) -2,
			(char) -2, (char) -2, (char) -2, (char) -2, (char) -2, (char) -2,
			(char) -2, (char) -2, (char) -2, (char) -2, (char) -2, (char) -2,
			(char) -2, (char) -2, (char) -2, (char) -2, (char) -2, (char) -2,
			(char) -2, '{', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			(char) -2, (char) -2, (char) -1, (char) -2, (char) -1, (char) -2,
			'}', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', (char) -2,
			(char) -2, (char) -2, (char) -2, (char) -2, (char) -2, '\\',
			(char) -2, 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', (char) -2,
			(char) -2, (char) -1, (char) -2, (char) -2, (char) -2, '0', '1',
			'2', '3', '4', '5', '6', '7', '8', '9', (char) -1, (char) -2,
			(char) -2, (char) -2, (char) -2, (char) -1 };

}
