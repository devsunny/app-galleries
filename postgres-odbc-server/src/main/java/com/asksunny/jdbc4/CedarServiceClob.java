package com.asksunny.jdbc4;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.SQLException;

public class CedarServiceClob implements Clob {

	private ClobWriter writer = new ClobWriter();

	public CedarServiceClob() {

	}

	@Override
	public long length() throws SQLException {
		return writer.getBuffer().length();
	}

	@Override
	public String getSubString(long pos, int length) throws SQLException {
		return writer.getBuffer().substring((int) pos, (int) (pos + length));
	}

	@Override
	public Reader getCharacterStream() throws SQLException {
		return new StringReader(writer.getBuffer().toString());
	}

	@Override
	public InputStream getAsciiStream() throws SQLException {

		return new ByteArrayInputStream(writer.getBuffer().toString()
				.getBytes(Charset.defaultCharset()));
	}

	@Override
	public long position(String searchstr, long start) throws SQLException {

		return writer.getBuffer().indexOf(searchstr, (int) start);
	}

	@Override
	public long position(Clob searchstr, long start) throws SQLException {
		String str = searchstr.getSubString(0, (int) searchstr.length());
		return position(str, start);
	}

	@Override
	public int setString(long pos, String str) throws SQLException {
		int start = (int) pos;
		int end = start + str.length();
		if (end > length()) {
			end = (int) length();
		}
		writer.getBuffer().replace(start, end, str);
		return str.length();
	}

	@Override
	public int setString(long pos, String str, int offset, int len)
			throws SQLException {
		String in = str.substring(offset, offset + len);
		return setString(pos, in);
	}

	@Override
	public OutputStream setAsciiStream(long pos) throws SQLException {
		return new ClobOutputStream(this.writer);
	}

	@Override
	public Writer setCharacterStream(long pos) throws SQLException {
		return this.writer;
	}

	@Override
	public void truncate(long len) throws SQLException {
		int blen = (int) length();
		this.writer.getBuffer().delete((int) len, blen);
	}

	@Override
	public void free() throws SQLException {
		this.writer.getBuffer().setLength(0);
	}

	@Override
	public Reader getCharacterStream(long pos, long length) throws SQLException {
		return new StringReader(this.writer.getBuffer().substring((int) pos,
				(int) (pos + length)));
	}

}
