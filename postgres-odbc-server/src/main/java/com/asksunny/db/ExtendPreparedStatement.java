package com.asksunny.db;

import java.sql.PreparedStatement;
import java.sql.Statement;

public class ExtendPreparedStatement implements PreparedStatement {

	protected java.sql.Statement wrappedObject = null;
	

	public ExtendPreparedStatement(PreparedStatement wrappedObject) {
		this.wrappedObject = wrappedObject;		
	}

	public ExtendPreparedStatement(Statement wrappedObject) {
		this.wrappedObject = wrappedObject;		
	}

	public Statement getWrappedObject() {
		return wrappedObject;
	}

	public void setBoolean(int arg0, boolean arg1) throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setBoolean(arg0, arg1);
	}

	public void setByte(int arg0, byte arg1) throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setByte(arg0, arg1);
	}

	public void setDouble(int arg0, double arg1) throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setDouble(arg0, arg1);
	}

	public void setFloat(int arg0, float arg1) throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setFloat(arg0, arg1);
	}

	public void setInt(int arg0, int arg1) throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setInt(arg0, arg1);
	}

	public void setLong(int arg0, long arg1) throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setLong(arg0, arg1);
	}

	public void setShort(int arg0, short arg1) throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setShort(arg0, arg1);
	}

	public void setTimestamp(int arg0, java.sql.Timestamp arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setTimestamp(arg0, arg1);
	}

	public void setTimestamp(int arg0, java.sql.Timestamp arg1,
			java.util.Calendar arg2) throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setTimestamp(arg0, arg1, arg2);
	}

	public void setURL(int arg0, java.net.URL arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setURL(arg0, arg1);
	}

	public void clearParameters() throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).clearParameters();
	}

	public int executeUpdate() throws java.sql.SQLException {
		return ((PreparedStatement) wrappedObject).executeUpdate();
	}

	public java.sql.ParameterMetaData getParameterMetaData()
			throws java.sql.SQLException {
		return ((PreparedStatement) wrappedObject).getParameterMetaData();
	}

	public void setAsciiStream(int arg0, java.io.InputStream arg1, long arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setAsciiStream(arg0, arg1, arg2);
	}

	public void setAsciiStream(int arg0, java.io.InputStream arg1, int arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setAsciiStream(arg0, arg1, arg2);
	}

	public void setAsciiStream(int arg0, java.io.InputStream arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setAsciiStream(arg0, arg1);
	}

	public void setBigDecimal(int arg0, java.math.BigDecimal arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setBigDecimal(arg0, arg1);
	}

	public void setBinaryStream(int arg0, java.io.InputStream arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setBinaryStream(arg0, arg1);
	}

	public void setBinaryStream(int arg0, java.io.InputStream arg1, long arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setBinaryStream(arg0, arg1, arg2);
	}

	public void setBinaryStream(int arg0, java.io.InputStream arg1, int arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setBinaryStream(arg0, arg1, arg2);
	}

	public void setCharacterStream(int arg0, java.io.Reader arg1, int arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject)
				.setCharacterStream(arg0, arg1, arg2);
	}

	public void setCharacterStream(int arg0, java.io.Reader arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setCharacterStream(arg0, arg1);
	}

	public void setCharacterStream(int arg0, java.io.Reader arg1, long arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject)
				.setCharacterStream(arg0, arg1, arg2);
	}

	public void setNCharacterStream(int arg0, java.io.Reader arg1, long arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setNCharacterStream(arg0, arg1,
				arg2);
	}

	public void setNCharacterStream(int arg0, java.io.Reader arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setNCharacterStream(arg0, arg1);
	}

	@Deprecated
	public void setUnicodeStream(int arg0, java.io.InputStream arg1, int arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setUnicodeStream(arg0, arg1, arg2);
	}

	public boolean execute() throws java.sql.SQLException {
		return ((PreparedStatement) wrappedObject).execute();
	}

	public java.sql.ResultSet executeQuery() throws java.sql.SQLException {
		return ((PreparedStatement) wrappedObject).executeQuery();
	}

	public void setBytes(int arg0, byte[] arg1) throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setBytes(arg0, arg1);
	}

	public java.sql.ResultSetMetaData getMetaData()
			throws java.sql.SQLException {
		return ((PreparedStatement) wrappedObject).getMetaData();
	}

	public void setBlob(int arg0, java.io.InputStream arg1, long arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setBlob(arg0, arg1, arg2);
	}

	public void setBlob(int arg0, java.io.InputStream arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setBlob(arg0, arg1);
	}

	public void setBlob(int arg0, java.sql.Blob arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setBlob(arg0, arg1);
	}

	public void addBatch() throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).addBatch();
	}

	public void setArray(int arg0, java.sql.Array arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setArray(arg0, arg1);
	}

	public void setClob(int arg0, java.io.Reader arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setClob(arg0, arg1);
	}

	public void setClob(int arg0, java.io.Reader arg1, long arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setClob(arg0, arg1, arg2);
	}

	public void setClob(int arg0, java.sql.Clob arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setClob(arg0, arg1);
	}

	public void setDate(int arg0, java.sql.Date arg1, java.util.Calendar arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setDate(arg0, arg1, arg2);
	}

	public void setDate(int arg0, java.sql.Date arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setDate(arg0, arg1);
	}

	public void setNClob(int arg0, java.io.Reader arg1, long arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setNClob(arg0, arg1, arg2);
	}

	public void setNClob(int arg0, java.io.Reader arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setNClob(arg0, arg1);
	}

	public void setNClob(int arg0, java.sql.NClob arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setNClob(arg0, arg1);
	}

	public void setNString(int arg0, java.lang.String arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setNString(arg0, arg1);
	}

	public void setNull(int arg0, int arg1, java.lang.String arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setNull(arg0, arg1, arg2);
	}

	public void setNull(int arg0, int arg1) throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setNull(arg0, arg1);
	}

	public void setObject(int arg0, java.lang.Object arg1, int arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setObject(arg0, arg1, arg2);
	}

	public void setObject(int arg0, java.lang.Object arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setObject(arg0, arg1);
	}

	public void setObject(int arg0, java.lang.Object arg1, int arg2, int arg3)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setObject(arg0, arg1, arg2, arg3);
	}

	public void setRef(int arg0, java.sql.Ref arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setRef(arg0, arg1);
	}

	public void setRowId(int arg0, java.sql.RowId arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setRowId(arg0, arg1);
	}

	public void setSQLXML(int arg0, java.sql.SQLXML arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setSQLXML(arg0, arg1);
	}

	public void setString(int arg0, java.lang.String arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setString(arg0, arg1);
	}

	public void setTime(int arg0, java.sql.Time arg1, java.util.Calendar arg2)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setTime(arg0, arg1, arg2);
	}

	public void setTime(int arg0, java.sql.Time arg1)
			throws java.sql.SQLException {
		((PreparedStatement) wrappedObject).setTime(arg0, arg1);
	}

	public void close() throws java.sql.SQLException {
		wrappedObject.close();
	}

	public int executeUpdate(java.lang.String arg0, int[] arg1)
			throws java.sql.SQLException {
		return wrappedObject.executeUpdate(arg0, arg1);
	}

	public int executeUpdate(java.lang.String arg0, int arg1)
			throws java.sql.SQLException {
		return wrappedObject.executeUpdate(arg0, arg1);
	}

	public int executeUpdate(java.lang.String arg0, String[] arg1)
			throws java.sql.SQLException {
		return wrappedObject.executeUpdate(arg0, arg1);
	}

	public int executeUpdate(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.executeUpdate(arg0);
	}

	public void clearWarnings() throws java.sql.SQLException {
		wrappedObject.clearWarnings();
	}

	public void closeOnCompletion() throws java.sql.SQLException {
		wrappedObject.closeOnCompletion();
	}

	public java.sql.Connection getConnection() throws java.sql.SQLException {
		return wrappedObject.getConnection();
	}

	public int getFetchDirection() throws java.sql.SQLException {
		return wrappedObject.getFetchDirection();
	}

	public java.sql.ResultSet getGeneratedKeys() throws java.sql.SQLException {
		return wrappedObject.getGeneratedKeys();
	}

	public int getMaxFieldSize() throws java.sql.SQLException {
		return wrappedObject.getMaxFieldSize();
	}

	public boolean getMoreResults() throws java.sql.SQLException {
		return wrappedObject.getMoreResults();
	}

	public boolean getMoreResults(int arg0) throws java.sql.SQLException {
		return wrappedObject.getMoreResults(arg0);
	}

	public int getQueryTimeout() throws java.sql.SQLException {
		return wrappedObject.getQueryTimeout();
	}

	public int getResultSetConcurrency() throws java.sql.SQLException {
		return wrappedObject.getResultSetConcurrency();
	}

	public int getResultSetHoldability() throws java.sql.SQLException {
		return wrappedObject.getResultSetHoldability();
	}

	public int getResultSetType() throws java.sql.SQLException {
		return wrappedObject.getResultSetType();
	}

	public int getUpdateCount() throws java.sql.SQLException {
		return wrappedObject.getUpdateCount();
	}

	public boolean isCloseOnCompletion() throws java.sql.SQLException {
		return wrappedObject.isCloseOnCompletion();
	}

	public void setCursorName(java.lang.String arg0)
			throws java.sql.SQLException {
		wrappedObject.setCursorName(arg0);
	}

	public void setEscapeProcessing(boolean arg0) throws java.sql.SQLException {
		wrappedObject.setEscapeProcessing(arg0);
	}

	public boolean execute(java.lang.String arg0, int arg1)
			throws java.sql.SQLException {
		return wrappedObject.execute(arg0, arg1);
	}

	public boolean execute(java.lang.String arg0, int[] arg1)
			throws java.sql.SQLException {
		return wrappedObject.execute(arg0, arg1);
	}

	public boolean execute(java.lang.String arg0, String[] arg1)
			throws java.sql.SQLException {
		return wrappedObject.execute(arg0, arg1);
	}

	public boolean execute(java.lang.String arg0) throws java.sql.SQLException {
		return wrappedObject.execute(arg0);
	}

	public java.sql.ResultSet executeQuery(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.executeQuery(arg0);
	}

	public void addBatch(java.lang.String arg0) throws java.sql.SQLException {
		wrappedObject.addBatch(arg0);
	}

	public void cancel() throws java.sql.SQLException {
		wrappedObject.cancel();
	}

	public void clearBatch() throws java.sql.SQLException {
		wrappedObject.clearBatch();
	}

	public int[] executeBatch() throws java.sql.SQLException {
		return wrappedObject.executeBatch();
	}

	public int getFetchSize() throws java.sql.SQLException {
		return wrappedObject.getFetchSize();
	}

	public int getMaxRows() throws java.sql.SQLException {
		return wrappedObject.getMaxRows();
	}

	public java.sql.ResultSet getResultSet() throws java.sql.SQLException {
		return wrappedObject.getResultSet();
	}

	public java.sql.SQLWarning getWarnings() throws java.sql.SQLException {
		return wrappedObject.getWarnings();
	}

	public boolean isClosed() throws java.sql.SQLException {
		return wrappedObject.isClosed();
	}

	public boolean isPoolable() throws java.sql.SQLException {
		return wrappedObject.isPoolable();
	}

	public void setFetchSize(int arg0) throws java.sql.SQLException {
		wrappedObject.setFetchSize(arg0);
	}

	public void setMaxRows(int arg0) throws java.sql.SQLException {
		wrappedObject.setMaxRows(arg0);
	}

	public void setPoolable(boolean arg0) throws java.sql.SQLException {
		wrappedObject.setPoolable(arg0);
	}

	public void setFetchDirection(int arg0) throws java.sql.SQLException {
		wrappedObject.setFetchDirection(arg0);
	}

	public void setMaxFieldSize(int arg0) throws java.sql.SQLException {
		wrappedObject.setMaxFieldSize(arg0);
	}

	public void setQueryTimeout(int arg0) throws java.sql.SQLException {
		wrappedObject.setQueryTimeout(arg0);
	}

	public <T> T unwrap(java.lang.Class<T> arg0) throws java.sql.SQLException {
		return wrappedObject.unwrap(arg0);
	}

	public boolean isWrapperFor(java.lang.Class<?> arg0)
			throws java.sql.SQLException {
		return wrappedObject.isWrapperFor(arg0);
	}

}
