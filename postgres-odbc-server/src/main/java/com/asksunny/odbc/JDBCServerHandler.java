package com.asksunny.odbc;

import java.util.Properties;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class JDBCServerHandler extends
		SimpleChannelInboundHandler<PostgresMessage> {

	private Properties connectionInfo;
	private boolean autoCommit = true;

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
			System.out.println("What Type:"
					+ (char) postgresMessage.getMessageType());
			switch (postgresMessage.getMessageType()) {
			case 'p':
				System.out.println("AUTH=+++++++++++++++++++++++++++++++");
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
							"8.1.4"));
					ctx.writeAndFlush(new NameValuePair(
							"session_authorization", connectionInfo
									.getProperty("user")));
					ctx.writeAndFlush(new NameValuePair(
							"standard_conforming_strings", "off")); // TODO
					ctx.writeAndFlush(new NameValuePair("TimeZone", "EST"));

					PostgresMessage keyData = new PostgresMessage('K', null);
					keyData.createBuffer().writeInt(1234).writeInt(4567);
					ctx.writeAndFlush(keyData);
					readyForQuery(ctx);
				} catch (Exception ex) {
					// Authentication failed;
					ex.printStackTrace();
					ctx.close(); // let kill the connection;
				}
				System.out
						.println("AUTH=---------------------------------------");
				break;
			case 'P':
				String name = postgresMessage.readString();
				String sql = postgresMessage.readString();

				break;
			case 'Q':
				String query = postgresMessage.readString();
				System.out.println("query=----" + query);
				commandCompleted(ctx, SQLCommandType.OTHER, 0);
				readyForQuery(ctx);
				break;
			case 'S':
			
				System.out.println("Sync=----");
				
				readyForQuery(ctx);
				break;
			case 'X': {
				close(ctx);
				break;
			}

			default:
				readyForQuery(ctx);
				break;
			}

		}

	}
	
	protected void readyForQuery(ChannelHandlerContext ctx) throws Exception {
		PostgresMessage readyForQuery = new PostgresMessage('Z',
				null);
		if (this.autoCommit) {
			readyForQuery.createBuffer().writeByte('I');
		} else {
			readyForQuery.createBuffer().writeByte('T');
		}
		ctx.writeAndFlush(readyForQuery);
	}
	
	protected void commandCompleted(ChannelHandlerContext ctx, SQLCommandType type, int updateCount) throws Exception {
		PostgresMessage cmdCompleted = new PostgresMessage('C',
				null);
		cmdCompleted.createBuffer();
		if(type==SQLCommandType.INSERT){
			cmdCompleted.writeString("INSERT 0 ", Integer.toString(updateCount));
		}else if(type==SQLCommandType.DELETE){
			cmdCompleted.writeString("DELETE ", Integer.toString(updateCount));
		}else if(type==SQLCommandType.SELECT || type==SQLCommandType.CALL){
			cmdCompleted.writeString("SELECT");
		}else if(type==SQLCommandType.BEGIN){
			cmdCompleted.writeString("BEGIN");
		}else{
			cmdCompleted.writeString("UPADTE 0 ", Integer.toString(updateCount));
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
