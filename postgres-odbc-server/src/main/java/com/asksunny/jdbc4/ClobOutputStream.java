package com.asksunny.jdbc4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

public class ClobOutputStream extends OutputStream {

	private ByteArrayOutputStream out = null;
	private ClobWriter writer = null;
	private boolean closed = false;

	public ClobOutputStream(ClobWriter writer) {
		out = new ByteArrayOutputStream();
		this.writer = writer;
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

	@Override
	public void flush() throws IOException {
		if (closed==false && this.out != null) {
			writer.write(new String(out.toByteArray(), Charset.defaultCharset()));
			out = new ByteArrayOutputStream();
		}
	}

	@Override
	public void close() throws IOException {
		if (closed==false && this.out != null) {
			writer.write(new String(out.toByteArray(), Charset.defaultCharset()));
			out.close();
			closed = true;
		}
	}

}
