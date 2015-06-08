package com.asksunny.netty.jdbc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public final class ResultSetSerDe {

	private ResultSetSerDe() {
	}

	public static List<String[]> readCompress(DataInputStream din,
			int rowDelimiter, int coldelimiter) throws Exception {
		return readCompress(din, (char) rowDelimiter, (char) coldelimiter);
	}

	// DATA ENVOLOPE Structure [4 bytes int length] + [data ]
	public static List<String[]> readCompress(DataInputStream din,
			char rowDelimiter, char coldelimiter) throws Exception {
		int dataLength = din.readInt();
		byte[] tmp = new byte[dataLength];
		din.readFully(tmp);
		GZIPInputStream gin = new GZIPInputStream(new ByteArrayInputStream(tmp));
		String data = IOUtils.toString(gin,
				StandardCharsets.UTF_8.displayName());
		String[] metas = StringUtils.split(data, rowDelimiter);
		ArrayList<String[]> metasList = new ArrayList<>();
		for (int i = 0; i < metas.length; i++) {
			String[] cols = StringUtils.split(metas[i], coldelimiter);
			metasList.add(cols);
		}
		return metasList;
	}

	public static int[] sendCompressMetaBase(String prefix,
			ChannelHandlerContext channelCtx, ResultSet rs, String rsId,
			int rowDelimiter, int coldelimiter) throws Exception {
		return sendCompressMetaBase(prefix, channelCtx, rs, rsId,
				(char) rowDelimiter, (char) coldelimiter);
	}

	public static int[] sendCompressMetaBase(String prefix,
			ChannelHandlerContext channelCtx, ResultSet rs, String rsId,
			char rowDelimiter, char coldelimiter) throws Exception {
		ByteBuf buf = channelCtx.alloc().buffer(1024);
		ByteBufOutputStream bout = new ByteBufOutputStream(buf);
		GZIPOutputStream zout = new GZIPOutputStream(bout);
		PrintWriter pw = new PrintWriter(zout);
		int[] types = serializeMetaBase(prefix, pw, rs, rsId, rowDelimiter,
				coldelimiter);
		pw.flush();
		zout.flush();
		zout.close();
		channelCtx.writeAndFlush(buf);
		return types;
	}

	public static void sendCompressMetaMore(String prefix,
			ChannelHandlerContext channelCtx, ResultSet rs, String rsId,
			int rowDelimiter, int coldelimiter) throws Exception {
		sendCompressMetaMore(prefix, channelCtx, rs, rsId, (char) rowDelimiter,
				(char) coldelimiter);
	}

	public static void sendCompressMetaMore(String prefix,
			ChannelHandlerContext channelCtx, ResultSet rs, String rsId,
			char rowDelimiter, char coldelimiter) throws Exception {
		ByteBuf buf = channelCtx.alloc().buffer(1024);
		ByteBufOutputStream bout = new ByteBufOutputStream(buf);
		GZIPOutputStream zout = new GZIPOutputStream(bout);
		PrintWriter pw = new PrintWriter(zout);
		serializeMetaMore(prefix, pw, rs, rsId, rowDelimiter, coldelimiter);
		pw.flush();
		zout.flush();
		zout.close();
		channelCtx.writeAndFlush(buf);
	}

	public static void sendCompressData(String prefix,
			ChannelHandlerContext channelCtx, ResultSet rs, int maxRows,
			int[] types, int rowDelimiter, int coldelimiter) throws Exception {
		sendCompressData(prefix, channelCtx, rs, maxRows, types,
				(char) rowDelimiter, (char) coldelimiter);
	}

	public static void sendCompressData(String prefix,
			ChannelHandlerContext channelCtx, ResultSet rs, int maxRows,
			int[] types, char rowDelimiter, char coldelimiter) throws Exception {
		ByteBuf buf = channelCtx.alloc().buffer(1024);
		ByteBufOutputStream bout = new ByteBufOutputStream(buf);
		GZIPOutputStream zout = new GZIPOutputStream(bout);
		PrintWriter pw = new PrintWriter(zout);
		serializeData(prefix, pw, rs, maxRows, types, rowDelimiter,
				coldelimiter);
		pw.flush();
		zout.flush();
		zout.close();
		channelCtx.writeAndFlush(buf);
	}

	public static int[] sendMetaBase(String prefix,
			ChannelHandlerContext channelCtx, ResultSet rs, String rsId,
			char rowDelimiter, char coldelimiter) throws Exception {
		ByteBuf buf = channelCtx.alloc().buffer(1024);
		ByteBufOutputStream bout = new ByteBufOutputStream(buf);
		PrintWriter pw = new PrintWriter(bout);
		int[] types = serializeMetaBase(prefix, pw, rs, rsId, rowDelimiter,
				coldelimiter);
		channelCtx.writeAndFlush(buf);
		pw.flush();
		return types;
	}

	public static void sendMetaMore(String prefix,
			ChannelHandlerContext channelCtx, ResultSet rs, String rsId,
			char rowDelimiter, char coldelimiter) throws Exception {
		ByteBuf buf = channelCtx.alloc().buffer(1024);
		ByteBufOutputStream bout = new ByteBufOutputStream(buf);
		PrintWriter pw = new PrintWriter(bout);
		serializeMetaMore(prefix, pw, rs, rsId, rowDelimiter, coldelimiter);
		pw.flush();
		channelCtx.writeAndFlush(buf);
	}

	public static void sendData(String prefix,
			ChannelHandlerContext channelCtx, ResultSet rs, int maxRows,
			int[] types, char rowDelimiter, char coldelimiter) throws Exception {
		ByteBuf buf = channelCtx.alloc().buffer(1024);
		ByteBufOutputStream bout = new ByteBufOutputStream(buf);
		PrintWriter pw = new PrintWriter(bout);
		serializeData(prefix, pw, rs, maxRows, types, rowDelimiter,
				coldelimiter);
		pw.flush();
		channelCtx.writeAndFlush(buf);
	}

	
	public static int[] serializeMetaBase(String prefix, PrintWriter pw,
			ResultSet rs, String rsId, int rowDelimiter, int coldelimiter)
			throws SQLException{
		return serializeMetaBase(prefix, pw,  rs,
				rsId, rowDelimiter, coldelimiter);
	}
	
	public static int[] serializeMetaBase(String prefix, PrintWriter pw,
			ResultSet rs, String rsId, char rowDelimiter, char coldelimiter)
			throws SQLException {
		if (prefix != null) {
			pw.print(prefix);
		}
		return serializeMetaBase(pw, rs, rsId, rowDelimiter, coldelimiter);
	}

	public static int[] serializeMetaBase(PrintWriter pw, ResultSet rs,
			String rsId, char rowDelimiter, char coldelimiter)
			throws SQLException {
		pw.print(rsId);
		pw.print(coldelimiter);
		pw.print(rs.getFetchSize());
		pw.print(coldelimiter);
		pw.print(rs.getFetchDirection());
		pw.print(coldelimiter);
		pw.print(rs.getConcurrency());
		pw.print(coldelimiter);
		pw.print(rs.getHoldability());
		pw.print(coldelimiter);
		pw.print(rs.getCursorName());
		pw.print(coldelimiter);
		ResultSetMetaData rsmd = rs.getMetaData();
		int colCount = rsmd.getColumnCount();
		pw.print(colCount);
		pw.print(rowDelimiter);
		int colIdx = 0;
		int[] types = new int[colCount];
		for (colIdx = 1; colIdx <= colCount; colIdx++) {
			types[colIdx - 1] = rsmd.getColumnType(colIdx);
			pw.print(rsmd.getColumnType(colIdx));
			if (colIdx < colCount) {
				pw.print(coldelimiter);
			}
		}
		pw.print(rowDelimiter);
		for (colIdx = 1; colIdx <= colCount; colIdx++) {
			pw.print(rsmd.getColumnLabel(colIdx));
			if (colIdx < colCount) {
				pw.print(coldelimiter);
			}
		}
		pw.print(rowDelimiter);
		for (colIdx = 1; colIdx <= colCount; colIdx++) {
			pw.print(rsmd.getColumnDisplaySize(colIdx));
			if (colIdx < colCount) {
				pw.print(coldelimiter);
			}
		}

		return types;
	}

	public static void serializeMetaMore(String prefix, PrintWriter pw,
			ResultSet rs, String rsId, char rowDelimiter, char coldelimiter)
			throws SQLException {
		if (prefix != null) {
			pw.print(prefix);
		}
		serializeMetaMore(pw, rs, rsId, rowDelimiter, coldelimiter);
	}

	public static void serializeMetaMore(PrintWriter pw, ResultSet rs,
			String rsId, char rowDelimiter, char coldelimiter)
			throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int colCount = rsmd.getColumnCount();
		int colIdx = 0;
		for (colIdx = 1; colIdx <= colCount; colIdx++) {
			pw.print(rsmd.getPrecision(colIdx));
			if (colIdx < colCount) {
				pw.print(coldelimiter);
			}
		}
		pw.print(rowDelimiter);
		for (colIdx = 1; colIdx <= colCount; colIdx++) {
			pw.print(rsmd.getScale(colIdx));
			if (colIdx < colCount) {
				pw.print(coldelimiter);
			}
		}
		pw.print(rowDelimiter);
		for (colIdx = 1; colIdx <= colCount; colIdx++) {
			pw.print(rsmd.getColumnClassName(colIdx));
			if (colIdx < colCount) {
				pw.print(coldelimiter);
			}
		}
		pw.flush();
	}

	public static void serializeData(String prefix, PrintWriter pw,
			ResultSet rs, int maxRows, int[] types, char rowDelimiter,
			char coldelimiter) throws SQLException {
		if (prefix != null) {
			pw.print(prefix);
		}
		serializeData(pw, rs, maxRows, types, rowDelimiter, coldelimiter);
	}

	public static void serializeData(PrintWriter pw, ResultSet rs, int maxRows,
			int[] types, char rowDelimiter, char coldelimiter)
			throws SQLException {
		int j = 0, i = 0, colidx = 0;
		int colCount = types.length;
		int lastIdx = colCount - 1;
		Object colObj = null;
		String colStr = null;
		byte[] binVal = null;
		Timestamp tsVal = null;
		for (i = 0; i < maxRows && rs.next(); i++) {
			if (i > 0) {
				pw.print(rowDelimiter);
			}
			for (j = 0; j < colCount; j++) {
				colidx = j + 1;
				switch (types[j]) {
				case Types.BOOLEAN:
				case Types.BIT:
					colObj = rs.getObject(colidx);
					if (colObj != null) {
						pw.print(rs.getBoolean(colidx) ? "1" : "0");
					}
					break;
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					tsVal = rs.getTimestamp(colidx);
					if (tsVal != null) {
						pw.print(tsVal.getTime());
					}
					break;
				case Types.BINARY:
				case Types.VARBINARY:
				case Types.LONGVARBINARY:
				case Types.BLOB:
					binVal = rs.getBytes(colidx);
					if (binVal != null) {
						pw.print(Base64.encodeBase64String(binVal));
					}
					break;
				case Types.JAVA_OBJECT:
					// TODO: Not supported now
					break;
				case Types.STRUCT:
					// TODO: Not supported now
					break;
				case Types.ARRAY:
					// TODO: Not supported now
					break;
				case Types.REF:
					// TODO: Not supported now
					break;
				case Types.DATALINK:
					// TODO: Not supported now
					break;
				case Types.ROWID:
					RowId rid = rs.getRowId(colidx);
					if (rid != null) {
						pw.print(Base64.encodeBase64String(rid.getBytes()));
					}
					break;
				case Types.SQLXML:
				case Types.TINYINT:
				case Types.SMALLINT:
				case Types.INTEGER:
				case Types.BIGINT:
				case Types.FLOAT:
				case Types.REAL:
				case Types.DOUBLE:
				case Types.NUMERIC:
				case Types.DECIMAL:
					colStr = rs.getString(colidx);
					if (colStr != null) {
						pw.print(colStr);
					}
					break;
				case Types.OTHER:
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
				case Types.NCHAR:
				case Types.NVARCHAR:
				case Types.LONGNVARCHAR:
				case Types.NCLOB:
				case Types.CLOB:
					colStr = rs.getString(colidx);
					if (colStr != null) {
						int l = colStr.length();
						for (int k = 0; k < l; k++) {
							if (!Character.isISOControl(colStr.charAt(k))) {
								pw.print(colStr.charAt(k));
							}
						}
					}
					break;
				}
				if (j < lastIdx) {
					pw.print(coldelimiter);
				}
			}

		}
		pw.flush();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Class<Types> tc = Types.class;
		Field[] fd = tc.getDeclaredFields();
		for (int i = 0; i < fd.length; i++) {
			System.out.printf("case Types.%s:\n\tbreak;\n", fd[i].getName());
		}

	}

}
