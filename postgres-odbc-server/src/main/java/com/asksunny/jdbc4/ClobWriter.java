package com.asksunny.jdbc4;

import java.io.IOException;
import java.io.Writer;

public class ClobWriter extends Writer {

	
	private StringBuilder buf = new StringBuilder();
	
	public ClobWriter() {		
	}

	public ClobWriter(Object lock) {
		super(lock);	
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		buf.append(cbuf, off, len);		
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {		
		
	}

	public StringBuilder getBuffer() {
		return buf;
	}

	public void setBuffer(StringBuilder buf) {
		this.buf = buf;
	}

}
