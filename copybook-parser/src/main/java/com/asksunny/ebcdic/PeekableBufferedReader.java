package com.asksunny.ebcdic;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

public class PeekableBufferedReader implements Closeable {

	private String line = null;
	BufferedReader bufferedReader = null;

	public PeekableBufferedReader(BufferedReader breader) {
		this.bufferedReader = breader;
	}

	public PeekableBufferedReader(Reader breader) {
		this.bufferedReader = (breader instanceof BufferedReader) ? (BufferedReader) breader
				: new BufferedReader(breader);
	}

	public String peekLine() throws IOException 
	{
		if (line == null) {
			line = bufferedReader.readLine();
			return line;
		} else {
			return line;
		}
	}

	public String readLine() throws IOException {
		if (line == null) {
			return bufferedReader.readLine();
		} else {
			String ret = line;
			line = null;
			return ret;
		}
	}

	public void close() throws IOException {
		bufferedReader.close();
	}

}
