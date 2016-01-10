package com.asksunny.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class LHReader extends Reader {

	private Reader reader;
	private int[] buffer = null;
	private int rindex = 0;
	private int windex = 0;
	private int bufferLength = 0;
	private boolean eof = false;

	public LHReader(int lhCnt, Reader preader) throws IOException {
		buffer = new int[lhCnt];
		this.reader = (preader instanceof BufferedReader) ? preader : new BufferedReader(preader);
		this.bufferLength = lhCnt;
		fillBuffer(this.bufferLength);
	}

	protected void fillBuffer(int num) throws IOException {

		for (int i = 0; i < num; i++) {
			int widx = windex % bufferLength;
			buffer[widx] = reader.read();			
			if (buffer[widx] == -1) {
				eof = true;
				break;
			} else {
				windex++;
			}
		}
	}

	public boolean peekMatch(char[] cmp, boolean ignoreCase) {
		if (cmp.length > bufferLength) {
			throw new IndexOutOfBoundsException("Look ahead buffer is too short");
		}
		int ridx = rindex;
		for (int i = 0; i < cmp.length; i++) {
			int c1 = buffer[(i + ridx) % bufferLength];
			char c = cmp[i];
			if (ignoreCase) {
				if (Character.toLowerCase(c) != Character.toLowerCase((char) c1)) {
					return false;
				}
			} else {
				if (c != c1) {
					return false;
				}
			}
		}
		return true;
	}

	public int peek(int idx) {
		//System.out.println(String.format("WINDEX:%d", windex));
		int rdx = rindex + idx;
		if (rdx > windex) {
			throw new IndexOutOfBoundsException("End of text stream.");
		}
		return buffer[rdx % bufferLength];
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int rlen = 0;
		if (len <= bufferLength) {
			for (int i = 0; i < len; i++) {
				if (buffer[rindex % bufferLength] == -1) {
					break;
				} else {
					cbuf[off + i] = (char) buffer[rindex % bufferLength];
					rlen++;
					rindex++;
				}
			}
			if (rlen == len) {
				fillBuffer(len);
			}
		} else {
			for (int i = 0; i < bufferLength; i++) {
				if (buffer[rindex % bufferLength] == -1) {
					break;
				} else {
					cbuf[off + i] = (char) buffer[rindex % bufferLength];
					rlen++;
				}
			}
			int llen = reader.read(cbuf, off + bufferLength, len - bufferLength);
			rlen = rlen + llen;
			if (rlen == len) {
				fillBuffer(bufferLength);
			}
		}
		if (eof && rlen == 0) {
			return -1;
		}
		return rlen;
	}

}
