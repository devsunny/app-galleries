package com.asksunny.odbc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.set.Set;
import net.sf.jsqlparser.statement.show.Show;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.jdbc4.BogusResultSetProvider;

/**
 * This class is impired by H2 Database PGServer and PGServerThread Class;
 * 
 * @author SunnyLiu
 * 
 */
public class JDBCServerHandler extends
		SimpleChannelInboundHandler<PostgresMessage> {

	public final static String DATABASE = "database";
	public final static String DATESTYLE = "DateStyle";
	public final static String CLIENT_ENCODING = "client_encoding";
	public final static String USER = "user";
	public final static String EXTRA_FLOAT_DIGITS = "extra_float_digits";
	public final static String TIMEZONE = "TimeZone";
	public final static String DEFAULT_CLIENT_ENCODING = "UTF8";
	private static Logger logger = LoggerFactory
			.getLogger(JDBCServerHandler.class);
	private Properties connectionInfo;
	private boolean autoCommit = true;
	private Charset clientEncodingCharSet = Charset.defaultCharset();

	private String preName = null;

	public JDBCServerHandler() {
		connectionInfo = new Properties();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx,
			PostgresMessage postgresMessage) throws Exception {
		PostgresMessage resp = new PostgresMessage();
		if (postgresMessage.getMessageType() == 0) {
			ByteBuf in = postgresMessage.getMessage();
			int val = in.readInt();
			// byte[] keyValPairs = in.array();
			int len = in.readableBytes();
			byte[] data = new byte[len];
			in.readBytes(data);
			int start = 0;
			String key = null;
			for (int i = 0; i < data.length; i++) {
				byte b = data[i];
				if (b == 0 || i == data.length - 1) {
					if (i == start)
						break;
					String strVal = new String(data, start, i - start);
					if (key == null) {
						key = strVal;
					} else {
						if (logger.isDebugEnabled())
							logger.debug("{}:{}", key, strVal);
						connectionInfo.setProperty(key, strVal);
						key = null;
					}
					start = i + 1;
				}

			}
			resp.setMessageType('R');
			ByteBuf buf = Unpooled.buffer();
			buf.writeInt(3);
			resp.setMessage(buf);
			ctx.writeAndFlush(resp);
		} else {
			if (logger.isDebugEnabled())
				logger.debug("Command Type:{}",
						(char) postgresMessage.getMessageType());
			switch (postgresMessage.getMessageType()) {
			case 'p':
				if (logger.isDebugEnabled())
					logger.info("authenication....");
				String password = postgresMessage.getMessage().toString(
						CharsetUtil.US_ASCII);
				connectionInfo.setProperty("password", password);
				try {
					// Do authentication here;
					PostgresMessage authOk = new PostgresMessage('R', null);
					authOk.createBuffer().writeInt(0);
					ctx.writeAndFlush(authOk);
					ctx.writeAndFlush(new NameValuePair("DateStyle", "ISO"));
					ctx.writeAndFlush(new NameValuePair("integer_datetimes",
							"off"));
					ctx.writeAndFlush(new NameValuePair("is_superuser", "off"));
					ctx.writeAndFlush(new NameValuePair("server_encoding",
							"SQL_ASCII"));
					ctx.writeAndFlush(new NameValuePair("server_version",
							"9.3"));
					ctx.writeAndFlush(new NameValuePair(
							"session_authorization", connectionInfo
									.getProperty("user")));
					ctx.writeAndFlush(new NameValuePair(
							"standard_conforming_strings", "off")); // TODO
					ctx.writeAndFlush(new NameValuePair("TimeZone",
							"America/New_York"));

					PostgresMessage keyData = new PostgresMessage('K', null);
					keyData.createBuffer().writeInt(1234).writeInt(4567);
					ctx.writeAndFlush(keyData);
					readyForQuery(ctx);
					if (logger.isDebugEnabled())
						logger.debug("authenication....OK");
				} catch (Exception ex) {
					logger.error("authenication....Failed", ex);
					ex.printStackTrace();
					ctx.close(); // let kill the connection;
				}
				break;
			case 'P':

				String name = postgresMessage.readString();
				String sql = postgresMessage.readString();
				preName = name;
				if (logger.isDebugEnabled())
					logger.debug("Prepare Query:{}[{}]", name, sql);
				int count = postgresMessage.getMessage().readShort();
				if (logger.isDebugEnabled())
					logger.debug("Prepare Query Parameter count:[{}]", count);
				for (int i = 0; i < count; i++) {
					int type = postgresMessage.getMessage().readInt();
					if (logger.isDebugEnabled())
						logger.debug("Prepare Query Parameter Type:[{}]", type);
				}
				try {
					// Invoke Java Prepare statement Here
					sendParseComplete(ctx);
				} catch (Exception e) {
					sendErrorResponse(ctx, e);
				}
				readyForQuery(ctx);
				break;
			case 'Q':
				String query = postgresMessage.readString().trim();
				if (logger.isDebugEnabled())
					logger.debug("Query:[{}]", query);
				int queryStatus = processQuery(ctx, query);
				switch (queryStatus) {
				case SQLCommandType.PG_QUERY_GET_CLIENT_PROP:
					if (logger.isDebugEnabled())
						logger.debug("PG_QUERY_GET_CLIENT_PROP:[{}]", query);
					break;
				case SQLCommandType.PG_QUERY_SHOW:
					if (logger.isDebugEnabled())
						logger.debug("PG_QUERY_SHOW:[{}]", query);
					break;
				case SQLCommandType.PG_QUERY_SELECT:
					if (logger.isDebugEnabled())
						logger.debug("PG_QUERY_SELECT:[{}]", query);
					break;
				default:
					sendNoData(ctx);
					break;
				}
				sendCommandCompleted(ctx, SQLCommandType.OTHER, 0);
				readyForQuery(ctx);
				break;
			case 'B':
				if (logger.isDebugEnabled())
					logger.debug("PG_QUERY Binding");
				String portalName = postgresMessage.readString();
				String prepName = postgresMessage.readString();

				if (prepName.equals(prepName)) {
					if (logger.isDebugEnabled())
						logger.debug("PG_QUERY found Binding:{}/{}",
								portalName, prepName);
				} else {
					if (logger.isDebugEnabled())
						logger.debug("PG_QUERY not found Binding:{}/{}",
								portalName, prepName);
				}

				int formatCodeCount = postgresMessage.getMessage().readShort();
				int[] formatCodes = new int[formatCodeCount];
				for (int i = 0; i < formatCodeCount; i++) {
					formatCodes[i] = postgresMessage.getMessage().readShort();
				}
				int paramCount = postgresMessage.getMessage().readShort();
				try {
					for (int i = 0; i < paramCount; i++) {
						// setParameter(prep.prep, prep.paramType[i], i,
						// formatCodes);
					}
				} catch (Exception e) {
					sendErrorResponse(ctx, e);
					break;
				}
				int resultCodeCount = postgresMessage.getMessage().readShort();
				int[] resultColumnFormat = new int[resultCodeCount];
				for (int i = 0; i < resultCodeCount; i++) {
					resultColumnFormat[i] = postgresMessage.getMessage()
							.readShort();
				}
				sendBindComplete(ctx);
				if (logger.isDebugEnabled())
					logger.debug("PG_QUERY Binding Completed");
				break;
			case 'C':
				char ctype = (char) postgresMessage.getMessage().readByte();
				String cname = postgresMessage.readString();
				if (logger.isDebugEnabled())
					logger.debug("Close command:[{}]{}", ctype, cname);
				if (ctype == 'S') {
					// Prepared p = prepared.remove(name);
					// if (p != null) {
					// /JdbcUtils.closeSilently(p.prep);
					// }
				} else if (ctype == 'P') {
					// portals.remove(name);
				} else {
					logger.warn("Only S or P accepted, unknow close type:[{}]",
							ctype);
					sendErrorResponse(ctx, "expected S or P");
					break;
				}
				sendCloseComplete(ctx);
				break;
			case 'D':
				char dtype = (char) postgresMessage.getMessage().readByte();
				String dname = postgresMessage.readString();
				if (logger.isDebugEnabled())
					logger.debug("Describe command:[{}]/[{}]", dtype, dname);
				if (dtype == 'S') {
					if (logger.isDebugEnabled())
						logger.debug(
								"Prepared Parameter description:[{}]/[{}]",
								dtype, dname);
					// Prepared p = prepared.get(name);
					// if (p == null) {
					// sendErrorResponse(ctx, "Prepared not found: " + name);
					// } else {
					// sendParameterDescription(p);
					// }
					sendErrorResponse(ctx, "Not implemented yet");
				} else if (dtype == 'P') {
					if (logger.isDebugEnabled())
						logger.debug("Portal Parameter description:[{}]/[{}]",
								dtype, dname);
					// Portal p = portals.get(name);
					// if (p == null) {
					// sendErrorResponse("Portal not found: " + name);
					// } else {
					// PreparedStatement prep = p.prep.prep;
					// try {
					// ResultSetMetaData meta = prep.getMetaData();
					// sendRowDescription(meta);
					// } catch (Exception e) {
					// sendErrorResponse(e);
					// }
					// }
					sendRowDescription(ctx, BogusResultSetProvider.newResultSet().getMetaData());
					sendErrorResponse(ctx, "Not implemented yet");
				} else {
					// server.trace("expected S or P, got " + type);
					sendErrorResponse(ctx, "expected S or P");
				}
				break;
			case 'E':
				String ename = postgresMessage.readString();
				if (logger.isDebugEnabled())
					logger.debug("Execute command:[{}]", ename);
				// send error response here is portal not exist;
				int maxRows = postgresMessage.getMessage().readShort();
				if (logger.isDebugEnabled())
					logger.debug("Max Rows:[{}]", maxRows);

				try {
					// prep.setMaxRows(maxRows);
					// setActiveRequest(prep);
					// boolean result = prep.execute();
					// if (result) {
					// try {
					// ResultSet rs = prep.getResultSet();
					// // the meta-data is sent in the prior 'Describe'
					// while (rs.next()) {
					// sendDataRow(rs);
					// }
					// sendCommandComplete(prep, 0);
					// } catch (Exception e) {
					// sendErrorResponse(e);
					// }
					// } else {
					// sendCommandComplete(prep, prep.getUpdateCount());
					// }
					ResultSet rs = BogusResultSetProvider.newResultSet();
					sendResultSet(ctx, rs);
					sendCommandCompleted(ctx, SQLCommandType.SELECT, 0);
				} catch (Exception e) {
					// if (prep.wasCancelled()) {
					// sendCancelQueryResponse();
					// } else {
					// sendErrorResponse(e);
					// }
					sendErrorResponse(ctx, e);
				} finally {
					// setActiveRequest(null);
				}
				break;
			case 'S':
				if (logger.isDebugEnabled())
					logger.debug("Sync Command");
				readyForQuery(ctx);
				break;
			case 'X': 
				close(ctx);
				break;
			default:
				logger.warn("Unhandled command:{}", postgresMessage
						.getMessage().toString(CharsetUtil.US_ASCII));
				readyForQuery(ctx);
				break;
			}

		}

	}

	protected int processQuery(ChannelHandlerContext ctx, String query) {
		int ret = 0;
		try {
			Statement stmt = CCJSqlParserUtil.parse(query);
			if (stmt instanceof Set) {
				Set set = (Set) stmt;
				if (logger.isDebugEnabled())
					logger.debug("SETTING: {} = {}", set.getName(),
							set.getValue());
				this.connectionInfo.put(set.getName(), set.getValue());
				if (set.getName().equalsIgnoreCase(CLIENT_ENCODING)) {
					this.setClientEncodingCharSet(set.getValue());
				}
				ret = SQLCommandType.PG_QUERY_SET_CLIENT_PROP;
			} else if (stmt instanceof Select) {
				PgSystemFunctionSearcher search = new PgSystemFunctionSearcher(
						"pg_client_encoding");
				if (search.search((Select) stmt)) {
					sendSettingQueryResponse(ctx, CLIENT_ENCODING);
					ret = SQLCommandType.PG_QUERY_GET_CLIENT_PROP;
				} else {
					sendResultSet(ctx, BogusResultSetProvider.newResultSet());
					ret = SQLCommandType.PG_QUERY_SELECT;
				}
			} else if (stmt instanceof Insert) {
				ret = SQLCommandType.PG_QUERY_INSERT;
			} else if (stmt instanceof Insert) {
				ret = SQLCommandType.PG_QUERY_DELETE;
			} else if (stmt instanceof Insert) {
				ret = SQLCommandType.PG_QUERY_UPDATE;
			} else if (stmt instanceof Show) {
				Show show = (Show) stmt;
				handleShowCommand(ctx, show.getName());
				ret = SQLCommandType.PG_QUERY_SHOW;
			} else {
				ret = SQLCommandType.PG_QUERY_OTHER;
			}
		} catch (Throwable t) {
			ret = SQLCommandType.PG_QUERY_PARSER_ERROR;
			logger.warn("Failed JSQLParser parsing", t);
			;
		}

		return ret;
	}

	protected void handleShowCommand(ChannelHandlerContext ctx,
			String settingName) throws Exception {
		if (settingName.equalsIgnoreCase("ALL")) {

		} else {
			String setting = ODBCServer.getServersettings().getSetting(
					settingName);
			if (setting != null) {
				sendSettingResponse(ctx, settingName, setting);
			} else {
				sendSettingResponse(ctx, settingName, "off");
			}
		}

	}
	
	
	protected void setParameter(PostgresMessage postgresMessage, PreparedStatement prep,   int pgType, int i, int[] formatCodes) throws SQLException, Exception {
        boolean text = (i >= formatCodes.length) || (formatCodes[i] == 0);
        int col = i + 1;
        int paramLen = postgresMessage.getMessage().readInt();
        if (paramLen == -1) {
            prep.setNull(col, Types.NULL);
        } else if (text) {
            // plain text
            byte[] data = new byte[paramLen];
            postgresMessage.getMessage().readBytes(data);
            //readFully(data);
            prep.setString(col, new String(data, getClientEncodingCharSet()));
        } else {
            // binary
            switch (pgType) {
            case PostgresTypes.PG_TYPE_INT2:
               // checkParamLength(4, paramLen);
                prep.setShort(col, postgresMessage.getMessage().readShort());
                break;
            case PostgresTypes.PG_TYPE_INT4:
               // checkParamLength(4, paramLen);
                prep.setInt(col, postgresMessage.getMessage().readInt());
                break;
            case PostgresTypes.PG_TYPE_INT8:
                //checkParamLength(8, paramLen);
                prep.setLong(col, postgresMessage.getMessage().readLong());
                break;
            case PostgresTypes.PG_TYPE_FLOAT4:
                //checkParamLength(4, paramLen);
                prep.setFloat(col, postgresMessage.getMessage().readFloat());
                break;
            case PostgresTypes.PG_TYPE_FLOAT8:
                //checkParamLength(8, paramLen);
                prep.setDouble(col, postgresMessage.getMessage().readDouble());
                break;
            case PostgresTypes.PG_TYPE_BYTEA:
            	byte[] data1 = new byte[paramLen];
                postgresMessage.getMessage().readBytes(data1);
                //readFully(data);
                prep.setBytes(col, data1);            	
                break;
            default:
               //("Binary format for type: "+pgType+" is unsupported");
            	byte[] data2 = new byte[paramLen];
                postgresMessage.getMessage().readBytes(data2);
                //readFully(data);
                prep.setString(col, new String(data2, getClientEncodingCharSet()));
            }
        }
    }
	
	

	protected void sendParameterDescription(ChannelHandlerContext ctx)
			throws Exception {
		try {
			PreparedStatement prep = null; // Prepared p; PreparedStatement prep
											// = p.prep;
			ParameterMetaData meta = prep.getParameterMetaData();
			int count = meta.getParameterCount();
			PostgresMessage message = new PostgresMessage('t');
			message.createBuffer().writeShort(count);

			for (int i = 0; i < count; i++) {
				int type;
				// if (p.paramType != null && p.paramType[i] != 0) {
				// type = p.paramType[i];
				// } else {
				type = PostgresTypes.PG_TYPE_VARCHAR;
				// }
				// server.checkType(type);
				message.getMessage().writeInt(type);
			}
			ctx.writeAndFlush(message);
		} catch (Exception e) {
			sendErrorResponse(ctx, e);
		}
	}

	protected void sendParseComplete(ChannelHandlerContext ctx)
			throws Exception {
		PostgresMessage message = new PostgresMessage('1');
		ctx.writeAndFlush(message);
	}

	protected void sendBindComplete(ChannelHandlerContext ctx) throws Exception {
		PostgresMessage message = new PostgresMessage('2');
		ctx.writeAndFlush(message);
	}

	protected void sendCloseComplete(ChannelHandlerContext ctx)
			throws Exception {
		PostgresMessage message = new PostgresMessage('3');
		ctx.writeAndFlush(message);
	}

	protected void sendSettingQueryResponse(ChannelHandlerContext ctx,
			String settingName) throws Exception {
		String setting = getConnectionInfo().getProperty(settingName,
				DEFAULT_CLIENT_ENCODING);
		sendSettingResponse(ctx, settingName, setting);
	}

	protected void sendSettingResponse(ChannelHandlerContext ctx,
			String settingName, String setting) throws Exception {

		PostgresMessage metadata = new PostgresMessage('T');
		metadata.createBuffer().writeShort(1);
		metadata.writeString(settingName.toUpperCase());
		// object ID
		metadata.getMessage().writeInt(0);
		// attribute number of the column
		metadata.getMessage().writeShort(0);
		// data type
		metadata.getMessage().writeInt(PostgresTypes.PG_TYPE_VARCHAR);
		// pg_type.typlen
		metadata.getMessage().writeShort(
				getTypeSize(PostgresTypes.PG_TYPE_VARCHAR,
						setting.getBytes().length + 1));
		// pg_attribute.atttypmod
		metadata.getMessage().writeInt(-1);
		// the format type: text = 0, binary = 1
		metadata.getMessage().writeShort(
				formatAsText(PostgresTypes.PG_TYPE_VARCHAR) ? 0 : 1);
		ctx.writeAndFlush(metadata);

		PostgresMessage dataRow = new PostgresMessage('D');
		dataRow.createBuffer().writeShort(1);
		byte[] data = setting.getBytes(getClientEncodingCharSet());
		dataRow.getMessage().writeInt(data.length);
		dataRow.getMessage().writeBytes(data);
		ctx.writeAndFlush(dataRow);

	}

	protected void sendNoData(ChannelHandlerContext ctx) throws Exception {
		PostgresMessage endOfData = new PostgresMessage('n');
		ctx.writeAndFlush(endOfData);
	}

	protected void sendResultSet(ChannelHandlerContext ctx, ResultSet rs)
			throws Exception {
		ResultSetMetaData meta = rs.getMetaData();
		try {
			sendRowDescription(ctx, meta);
			while (rs.next()) {
				sendDataRow(ctx, rs);
			}
			sendCommandCompleted(ctx, SQLCommandType.SELECT, 0);
		} catch (Exception e) {
			sendErrorResponse(ctx, e);
		}
	}

	protected void sendDataRow(ChannelHandlerContext ctx, ResultSet rs)
			throws Exception {
		ResultSetMetaData metaData = rs.getMetaData();
		int columns = metaData.getColumnCount();
		PostgresMessage dataRow = new PostgresMessage('D');
		dataRow.createBuffer().writeShort(columns);
		for (int i = 1; i <= columns; i++) {
			writeDataColumn(dataRow, rs, i,
					PostgresTypes.convertType(metaData.getColumnType(i)));
		}
		ctx.writeAndFlush(dataRow);
	}

	private void writeDataColumn(PostgresMessage dataRow, ResultSet rs,
			int column, int pgType) throws Exception {
		if (formatAsText(pgType)) {
			// plain text
			switch (pgType) {
			case PostgresTypes.PG_TYPE_BOOL:
				dataRow.getMessage().writeInt(1);
				dataRow.getMessage().writeByte(
						rs.getBoolean(column) ? 't' : 'f');
				break;
			default:
				String s = rs.getString(column);
				if (s == null) {
					dataRow.getMessage().writeInt(-1);
				} else {
					byte[] data = s.getBytes(getClientEncodingCharSet());
					dataRow.getMessage().writeInt(data.length);
					dataRow.getMessage().writeBytes(data);
				}
			}
		} else {
			// binary
			switch (pgType) {
			case PostgresTypes.PG_TYPE_INT2:
				dataRow.getMessage().writeInt(2);
				dataRow.getMessage().writeShort(rs.getShort(column));
				break;
			case PostgresTypes.PG_TYPE_INT4:
				dataRow.getMessage().writeInt(4);
				dataRow.getMessage().writeInt(rs.getInt(column));
				break;
			case PostgresTypes.PG_TYPE_INT8:
				dataRow.getMessage().writeInt(8);
				dataRow.getMessage().writeLong(rs.getLong(column));
				break;
			case PostgresTypes.PG_TYPE_FLOAT4:
				dataRow.getMessage().writeInt(4);
				dataRow.getMessage().writeFloat(rs.getFloat(column));
				break;
			case PostgresTypes.PG_TYPE_FLOAT8:
				dataRow.getMessage().writeInt(8);
				dataRow.getMessage().writeDouble(rs.getDouble(column));
				break;
			case PostgresTypes.PG_TYPE_BYTEA:
				byte[] data = rs.getBytes(column);
				if (data == null) {
					dataRow.getMessage().writeInt(-1);
				} else {
					dataRow.getMessage().writeInt(data.length);
					dataRow.getMessage().writeBytes(data);
				}
				break;
			default:
				throw new IllegalStateException(
						"output binary format is undefined");
			}
		}
	}

	public Charset getClientEncodingCharSet() {
		return clientEncodingCharSet;
	}

	public void setClientEncodingCharSet(Charset clientEncodingCharSet) {
		this.clientEncodingCharSet = clientEncodingCharSet;
	}

	public void setClientEncodingCharSet(String clientEncoding) {
		try {
			if (Charset.isSupported(clientEncoding)) {
				this.clientEncodingCharSet = Charset.forName(clientEncoding);
			}

		} catch (Exception ex) {
			this.clientEncodingCharSet = Charset.defaultCharset();
		}
	}

	protected void sendRowDescription(ChannelHandlerContext ctx,
			ResultSetMetaData meta) throws Exception {
		if (meta == null) {
			sendNoData(ctx);
		} else {
			int columns = meta.getColumnCount();
			int[] types = new int[columns];
			int[] precision = new int[columns];
			String[] names = new String[columns];
			for (int i = 0; i < columns; i++) {
				String name = meta.getColumnName(i + 1);
				names[i] = name;
				int type = meta.getColumnType(i + 1);
				int pgType = PostgresTypes.convertType(type);
				// the ODBC client needs the column pg_catalog.pg_index
				// to be of type 'int2vector'
				// if (name.equalsIgnoreCase("indkey") &&
				// "pg_index".equalsIgnoreCase(
				// meta.getTableName(i + 1))) {
				// type = PgServer.PG_TYPE_INT2VECTOR;
				// }
				precision[i] = meta.getColumnDisplaySize(i + 1);
				if (type != Types.NULL) {
					// server.checkType(pgType);
				}
				types[i] = pgType;
			}
			PostgresMessage metadata = new PostgresMessage('T');
			metadata.createBuffer().writeShort(columns);
			for (int i = 0; i < columns; i++) {
				metadata.writeString(names[i].toUpperCase());
				// object ID
				metadata.getMessage().writeInt(0);
				// attribute number of the column
				metadata.getMessage().writeShort(0);
				// data type
				metadata.getMessage().writeInt(types[i]);
				// pg_type.typlen
				metadata.getMessage().writeShort(
						getTypeSize(types[i], precision[i]));
				// pg_attribute.atttypmod
				metadata.getMessage().writeInt(-1);
				// the format type: text = 0, binary = 1
				metadata.getMessage()
						.writeShort(formatAsText(types[i]) ? 0 : 1);
			}
			ctx.writeAndFlush(metadata);
		}
	}

	private static boolean formatAsText(int pgType) {
		switch (pgType) {
		// TODO: add more types to send as binary once compatibility is
		// confirmed
		case PostgresTypes.PG_TYPE_BYTEA:
			return false;
		}
		return true;
	}

	private static int getTypeSize(int pgType, int precision) {
		switch (pgType) {
		case PostgresTypes.PG_TYPE_BOOL:
			return 1;
		case PostgresTypes.PG_TYPE_VARCHAR:
			return Math.max(255, precision + 10);
		default:
			return precision + 4;
		}
	}

	protected void sendErrorResponse(ChannelHandlerContext ctx, Exception re)
			throws Exception {
		SQLException sex = new SQLException(re.toString(), "CDS999", 9999);
		sendErrorResponse(ctx, sex);

	}

	protected void sendErrorResponse(ChannelHandlerContext ctx, SQLException re)
			throws Exception {
		PostgresMessage errorMessage = new PostgresMessage('E');
		errorMessage.initMessage().writeByte('S').writeString("ERROR")
				.writeByte('C').writeString(re.getSQLState()).writeByte('M')
				.writeString(re.getMessage()).writeByte('D')
				.writeString(re.toString());
		ctx.writeAndFlush(errorMessage);
	}

	protected void sendErrorResponse(ChannelHandlerContext ctx, String message)
			throws Exception {
		PostgresMessage errorMessage = new PostgresMessage('E');
		errorMessage.initMessage().writeByte('S').writeString("ERROR")
				.writeByte('C').writeString("08P01").writeByte('M')
				.writeString(message);
		ctx.writeAndFlush(errorMessage);

	}

	protected void readyForQuery(ChannelHandlerContext ctx) throws Exception {
		PostgresMessage readyForQuery = new PostgresMessage('Z', null);
		if (this.autoCommit) {
			readyForQuery.createBuffer().writeByte('I');
		} else {
			readyForQuery.createBuffer().writeByte('T');
		}
		ctx.writeAndFlush(readyForQuery);
	}

	protected void sendCommandCompleted(ChannelHandlerContext ctx,
			SQLCommandType type, int updateCount) throws Exception {
		PostgresMessage cmdCompleted = new PostgresMessage('C', null);
		cmdCompleted.createBuffer();
		if (type == SQLCommandType.INSERT) {
			cmdCompleted
					.writeString("INSERT 0 ", Integer.toString(updateCount));
		} else if (type == SQLCommandType.DELETE) {
			cmdCompleted.writeString("DELETE ", Integer.toString(updateCount));
		} else if (type == SQLCommandType.SELECT || type == SQLCommandType.CALL) {
			cmdCompleted.writeString("SELECT");
		} else if (type == SQLCommandType.BEGIN) {
			cmdCompleted.writeString("BEGIN");
		} else {
			cmdCompleted
					.writeString("UPADTE 0 ", Integer.toString(updateCount));
		}
		ctx.writeAndFlush(cmdCompleted);
	}

	protected void close(ChannelHandlerContext ctx) throws Exception {
		// Release all resource
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
	}

	public Properties getConnectionInfo() {
		return connectionInfo;
	}

	public void setConnectionInfo(Properties connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

}
