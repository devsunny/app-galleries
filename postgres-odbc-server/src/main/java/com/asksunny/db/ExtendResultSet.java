package com.asksunny.db;

import java.sql.ResultSet;
import java.util.HashMap;

public class ExtendResultSet implements ResultSet {

	private HashMap<String, ? extends ExtendSqlFunction> sqlFunctionMaps = null;
	protected java.sql.ResultSet wrappedObject = null;
	private boolean postApply = false;
	private boolean closeParent = false;

	public ExtendResultSet(ResultSet wrappedObject, boolean closeParent,
			HashMap<String, ? extends ExtendSqlFunction> sqlFunctionMaps) {
		super();
		this.wrappedObject = wrappedObject;
		this.closeParent = closeParent;
		this.sqlFunctionMaps = sqlFunctionMaps;
	}

	public ExtendResultSet(java.sql.ResultSet wrappedObject,
			HashMap<String, ? extends ExtendSqlFunction> sqlFunctionMaps) {
		this(wrappedObject, false, sqlFunctionMaps);
	}

	public HashMap<String, ? extends ExtendSqlFunction> getSqlFunctionMaps() {
		return sqlFunctionMaps;
	}

	public void setSqlFunctionMaps(
			HashMap<String, ? extends ExtendSqlFunction> sqlFunctionMaps) {
		this.sqlFunctionMaps = sqlFunctionMaps;
	}

	public java.sql.ResultSet getWrappedObject() {
		return wrappedObject;
	}

	public boolean doPostApply() {
		return postApply;
	}

	public void setPostApply(boolean postApply) {
		this.postApply = postApply;
	}

	public java.lang.Object getObject(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getObject(arg0);
	}

	public java.lang.Object getObject(int arg0) throws java.sql.SQLException {
		return wrappedObject.getObject(arg0);
	}

	public java.lang.Object getObject(java.lang.String arg0,
			java.util.Map<java.lang.String, java.lang.Class<?>> arg1)
			throws java.sql.SQLException {
		return wrappedObject.getObject(arg0, arg1);
	}

	public <T> T getObject(java.lang.String arg0, java.lang.Class<T> arg1)
			throws java.sql.SQLException {
		return wrappedObject.getObject(arg0, arg1);
	}

	public <T> T getObject(int arg0, java.lang.Class<T> arg1)
			throws java.sql.SQLException {
		return wrappedObject.getObject(arg0, arg1);
	}

	public java.lang.Object getObject(int arg0,
			java.util.Map<java.lang.String, java.lang.Class<?>> arg1)
			throws java.sql.SQLException {
		return wrappedObject.getObject(arg0, arg1);
	}

	public boolean getBoolean(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getBoolean(arg0);
	}

	public boolean getBoolean(int arg0) throws java.sql.SQLException {
		return wrappedObject.getBoolean(arg0);
	}

	public byte getByte(int arg0) throws java.sql.SQLException {
		return wrappedObject.getByte(arg0);
	}

	public byte getByte(java.lang.String arg0) throws java.sql.SQLException {
		return wrappedObject.getByte(arg0);
	}

	public short getShort(java.lang.String arg0) throws java.sql.SQLException {
		return wrappedObject.getShort(arg0);
	}

	public short getShort(int arg0) throws java.sql.SQLException {
		return wrappedObject.getShort(arg0);
	}

	public int getInt(java.lang.String arg0) throws java.sql.SQLException {
		return wrappedObject.getInt(arg0);
	}

	public int getInt(int arg0) throws java.sql.SQLException {
		return wrappedObject.getInt(arg0);
	}

	public long getLong(java.lang.String arg0) throws java.sql.SQLException {
		return wrappedObject.getLong(arg0);
	}

	public long getLong(int arg0) throws java.sql.SQLException {
		return wrappedObject.getLong(arg0);
	}

	public float getFloat(int arg0) throws java.sql.SQLException {
		return wrappedObject.getFloat(arg0);
	}

	public float getFloat(java.lang.String arg0) throws java.sql.SQLException {
		return wrappedObject.getFloat(arg0);
	}

	public double getDouble(int arg0) throws java.sql.SQLException {
		return wrappedObject.getDouble(arg0);
	}

	public double getDouble(java.lang.String arg0) throws java.sql.SQLException {
		return wrappedObject.getDouble(arg0);
	}

	public java.sql.Array getArray(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getArray(arg0);
	}

	public java.sql.Array getArray(int arg0) throws java.sql.SQLException {
		return wrappedObject.getArray(arg0);
	}

	public boolean next() throws java.sql.SQLException {
		return wrappedObject.next();
	}

	public java.net.URL getURL(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getURL(arg0);
	}

	public java.net.URL getURL(int arg0) throws java.sql.SQLException {
		return wrappedObject.getURL(arg0);
	}

	public void close() throws java.sql.SQLException {
		try{
		wrappedObject.close();
		}finally{
			if(closeParent){
				wrappedObject.getStatement().close();
			}
		}
	}

	public int getType() throws java.sql.SQLException {
		return wrappedObject.getType();
	}

	public boolean previous() throws java.sql.SQLException {
		return wrappedObject.previous();
	}

	public byte[] getBytes(java.lang.String arg0) throws java.sql.SQLException {
		return wrappedObject.getBytes(arg0);
	}

	public byte[] getBytes(int arg0) throws java.sql.SQLException {
		return wrappedObject.getBytes(arg0);
	}

	public java.lang.String getString(int arg0) throws java.sql.SQLException {
		return wrappedObject.getString(arg0);
	}

	public java.lang.String getString(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getString(arg0);
	}

	public java.sql.Ref getRef(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getRef(arg0);
	}

	public java.sql.Ref getRef(int arg0) throws java.sql.SQLException {
		return wrappedObject.getRef(arg0);
	}

	public void cancelRowUpdates() throws java.sql.SQLException {
		wrappedObject.cancelRowUpdates();
	}

	public void clearWarnings() throws java.sql.SQLException {
		wrappedObject.clearWarnings();
	}

	public java.io.InputStream getAsciiStream(int arg0)
			throws java.sql.SQLException {
		return wrappedObject.getAsciiStream(arg0);
	}

	public java.io.InputStream getAsciiStream(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getAsciiStream(arg0);
	}

	public java.math.BigDecimal getBigDecimal(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getBigDecimal(arg0);
	}

	public java.math.BigDecimal getBigDecimal(int arg0)
			throws java.sql.SQLException {
		return wrappedObject.getBigDecimal(arg0);
	}

	@Deprecated
	public java.math.BigDecimal getBigDecimal(int arg0, int arg1)
			throws java.sql.SQLException {
		return wrappedObject.getBigDecimal(arg0, arg1);
	}

	@Deprecated
	public java.math.BigDecimal getBigDecimal(java.lang.String arg0, int arg1)
			throws java.sql.SQLException {
		return wrappedObject.getBigDecimal(arg0, arg1);
	}

	public java.io.InputStream getBinaryStream(int arg0)
			throws java.sql.SQLException {
		return wrappedObject.getBinaryStream(arg0);
	}

	public java.io.InputStream getBinaryStream(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getBinaryStream(arg0);
	}

	public java.io.Reader getCharacterStream(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getCharacterStream(arg0);
	}

	public java.io.Reader getCharacterStream(int arg0)
			throws java.sql.SQLException {
		return wrappedObject.getCharacterStream(arg0);
	}

	public int getConcurrency() throws java.sql.SQLException {
		return wrappedObject.getConcurrency();
	}

	public java.lang.String getCursorName() throws java.sql.SQLException {
		return wrappedObject.getCursorName();
	}

	public int getFetchDirection() throws java.sql.SQLException {
		return wrappedObject.getFetchDirection();
	}

	public int getHoldability() throws java.sql.SQLException {
		return wrappedObject.getHoldability();
	}

	public java.io.Reader getNCharacterStream(int arg0)
			throws java.sql.SQLException {
		return wrappedObject.getNCharacterStream(arg0);
	}

	public java.io.Reader getNCharacterStream(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getNCharacterStream(arg0);
	}

	@Deprecated
	public java.io.InputStream getUnicodeStream(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getUnicodeStream(arg0);
	}

	@Deprecated
	public java.io.InputStream getUnicodeStream(int arg0)
			throws java.sql.SQLException {
		return wrappedObject.getUnicodeStream(arg0);
	}

	public boolean isBeforeFirst() throws java.sql.SQLException {
		return wrappedObject.isBeforeFirst();
	}

	public void moveToCurrentRow() throws java.sql.SQLException {
		wrappedObject.moveToCurrentRow();
	}

	public void moveToInsertRow() throws java.sql.SQLException {
		wrappedObject.moveToInsertRow();
	}

	public void setFetchDirection(int arg0) throws java.sql.SQLException {
		wrappedObject.setFetchDirection(arg0);
	}

	public void updateAsciiStream(int arg0, java.io.InputStream arg1, int arg2)
			throws java.sql.SQLException {
		wrappedObject.updateAsciiStream(arg0, arg1, arg2);
	}

	public void updateAsciiStream(java.lang.String arg0,
			java.io.InputStream arg1, int arg2) throws java.sql.SQLException {
		wrappedObject.updateAsciiStream(arg0, arg1, arg2);
	}

	public void updateAsciiStream(int arg0, java.io.InputStream arg1, long arg2)
			throws java.sql.SQLException {
		wrappedObject.updateAsciiStream(arg0, arg1, arg2);
	}

	public void updateAsciiStream(int arg0, java.io.InputStream arg1)
			throws java.sql.SQLException {
		wrappedObject.updateAsciiStream(arg0, arg1);
	}

	public void updateAsciiStream(java.lang.String arg0,
			java.io.InputStream arg1) throws java.sql.SQLException {
		wrappedObject.updateAsciiStream(arg0, arg1);
	}

	public void updateAsciiStream(java.lang.String arg0,
			java.io.InputStream arg1, long arg2) throws java.sql.SQLException {
		wrappedObject.updateAsciiStream(arg0, arg1, arg2);
	}

	public void updateBigDecimal(int arg0, java.math.BigDecimal arg1)
			throws java.sql.SQLException {
		wrappedObject.updateBigDecimal(arg0, arg1);
	}

	public void updateBigDecimal(java.lang.String arg0,
			java.math.BigDecimal arg1) throws java.sql.SQLException {
		wrappedObject.updateBigDecimal(arg0, arg1);
	}

	public void updateBinaryStream(java.lang.String arg0,
			java.io.InputStream arg1) throws java.sql.SQLException {
		wrappedObject.updateBinaryStream(arg0, arg1);
	}

	public void updateBinaryStream(int arg0, java.io.InputStream arg1, int arg2)
			throws java.sql.SQLException {
		wrappedObject.updateBinaryStream(arg0, arg1, arg2);
	}

	public void updateBinaryStream(int arg0, java.io.InputStream arg1)
			throws java.sql.SQLException {
		wrappedObject.updateBinaryStream(arg0, arg1);
	}

	public void updateBinaryStream(java.lang.String arg0,
			java.io.InputStream arg1, long arg2) throws java.sql.SQLException {
		wrappedObject.updateBinaryStream(arg0, arg1, arg2);
	}

	public void updateBinaryStream(int arg0, java.io.InputStream arg1, long arg2)
			throws java.sql.SQLException {
		wrappedObject.updateBinaryStream(arg0, arg1, arg2);
	}

	public void updateBinaryStream(java.lang.String arg0,
			java.io.InputStream arg1, int arg2) throws java.sql.SQLException {
		wrappedObject.updateBinaryStream(arg0, arg1, arg2);
	}

	public void updateBoolean(java.lang.String arg0, boolean arg1)
			throws java.sql.SQLException {
		wrappedObject.updateBoolean(arg0, arg1);
	}

	public void updateBoolean(int arg0, boolean arg1)
			throws java.sql.SQLException {
		wrappedObject.updateBoolean(arg0, arg1);
	}

	public void updateCharacterStream(java.lang.String arg0,
			java.io.Reader arg1, long arg2) throws java.sql.SQLException {
		wrappedObject.updateCharacterStream(arg0, arg1, arg2);
	}

	public void updateCharacterStream(int arg0, java.io.Reader arg1, int arg2)
			throws java.sql.SQLException {
		wrappedObject.updateCharacterStream(arg0, arg1, arg2);
	}

	public void updateCharacterStream(java.lang.String arg0,
			java.io.Reader arg1, int arg2) throws java.sql.SQLException {
		wrappedObject.updateCharacterStream(arg0, arg1, arg2);
	}

	public void updateCharacterStream(java.lang.String arg0, java.io.Reader arg1)
			throws java.sql.SQLException {
		wrappedObject.updateCharacterStream(arg0, arg1);
	}

	public void updateCharacterStream(int arg0, java.io.Reader arg1)
			throws java.sql.SQLException {
		wrappedObject.updateCharacterStream(arg0, arg1);
	}

	public void updateCharacterStream(int arg0, java.io.Reader arg1, long arg2)
			throws java.sql.SQLException {
		wrappedObject.updateCharacterStream(arg0, arg1, arg2);
	}

	public void updateNCharacterStream(java.lang.String arg0,
			java.io.Reader arg1, long arg2) throws java.sql.SQLException {
		wrappedObject.updateNCharacterStream(arg0, arg1, arg2);
	}

	public void updateNCharacterStream(java.lang.String arg0,
			java.io.Reader arg1) throws java.sql.SQLException {
		wrappedObject.updateNCharacterStream(arg0, arg1);
	}

	public void updateNCharacterStream(int arg0, java.io.Reader arg1)
			throws java.sql.SQLException {
		wrappedObject.updateNCharacterStream(arg0, arg1);
	}

	public void updateNCharacterStream(int arg0, java.io.Reader arg1, long arg2)
			throws java.sql.SQLException {
		wrappedObject.updateNCharacterStream(arg0, arg1, arg2);
	}

	public void updateNString(int arg0, java.lang.String arg1)
			throws java.sql.SQLException {
		wrappedObject.updateNString(arg0, arg1);
	}

	public void updateNString(java.lang.String arg0, java.lang.String arg1)
			throws java.sql.SQLException {
		wrappedObject.updateNString(arg0, arg1);
	}

	public void updateTimestamp(int arg0, java.sql.Timestamp arg1)
			throws java.sql.SQLException {
		wrappedObject.updateTimestamp(arg0, arg1);
	}

	public void updateTimestamp(java.lang.String arg0, java.sql.Timestamp arg1)
			throws java.sql.SQLException {
		wrappedObject.updateTimestamp(arg0, arg1);
	}

	public java.sql.Date getDate(java.lang.String arg0, java.util.Calendar arg1)
			throws java.sql.SQLException {
		return wrappedObject.getDate(arg0, arg1);
	}

	public java.sql.Date getDate(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getDate(arg0);
	}

	public java.sql.Date getDate(int arg0) throws java.sql.SQLException {
		return wrappedObject.getDate(arg0);
	}

	public java.sql.Date getDate(int arg0, java.util.Calendar arg1)
			throws java.sql.SQLException {
		return wrappedObject.getDate(arg0, arg1);
	}

	public boolean absolute(int arg0) throws java.sql.SQLException {
		return wrappedObject.absolute(arg0);
	}

	public int findColumn(java.lang.String arg0) throws java.sql.SQLException {
		return wrappedObject.findColumn(arg0);
	}

	public void afterLast() throws java.sql.SQLException {
		wrappedObject.afterLast();
	}

	public void deleteRow() throws java.sql.SQLException {
		wrappedObject.deleteRow();
	}

	public void beforeFirst() throws java.sql.SQLException {
		wrappedObject.beforeFirst();
	}

	public boolean first() throws java.sql.SQLException {
		return wrappedObject.first();
	}

	public java.sql.Blob getBlob(int arg0) throws java.sql.SQLException {
		return wrappedObject.getBlob(arg0);
	}

	public java.sql.Blob getBlob(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getBlob(arg0);
	}

	public java.sql.Clob getClob(int arg0) throws java.sql.SQLException {
		return wrappedObject.getClob(arg0);
	}

	public java.sql.Clob getClob(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getClob(arg0);
	}

	public int getFetchSize() throws java.sql.SQLException {
		return wrappedObject.getFetchSize();
	}

	public java.sql.ResultSetMetaData getMetaData()
			throws java.sql.SQLException {
		return wrappedObject.getMetaData();
	}

	public java.sql.NClob getNClob(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getNClob(arg0);
	}

	public java.sql.NClob getNClob(int arg0) throws java.sql.SQLException {
		return wrappedObject.getNClob(arg0);
	}

	public java.lang.String getNString(int arg0) throws java.sql.SQLException {
		return wrappedObject.getNString(arg0);
	}

	public java.lang.String getNString(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getNString(arg0);
	}

	public int getRow() throws java.sql.SQLException {
		return wrappedObject.getRow();
	}

	public java.sql.RowId getRowId(int arg0) throws java.sql.SQLException {
		return wrappedObject.getRowId(arg0);
	}

	public java.sql.RowId getRowId(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getRowId(arg0);
	}

	public java.sql.SQLXML getSQLXML(int arg0) throws java.sql.SQLException {
		return wrappedObject.getSQLXML(arg0);
	}

	public java.sql.SQLXML getSQLXML(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getSQLXML(arg0);
	}

	public java.sql.Statement getStatement() throws java.sql.SQLException {
		return wrappedObject.getStatement();
	}

	public java.sql.Time getTime(int arg0, java.util.Calendar arg1)
			throws java.sql.SQLException {
		return wrappedObject.getTime(arg0, arg1);
	}

	public java.sql.Time getTime(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getTime(arg0);
	}

	public java.sql.Time getTime(java.lang.String arg0, java.util.Calendar arg1)
			throws java.sql.SQLException {
		return wrappedObject.getTime(arg0, arg1);
	}

	public java.sql.Time getTime(int arg0) throws java.sql.SQLException {
		return wrappedObject.getTime(arg0);
	}

	public java.sql.Timestamp getTimestamp(java.lang.String arg0,
			java.util.Calendar arg1) throws java.sql.SQLException {
		return wrappedObject.getTimestamp(arg0, arg1);
	}

	public java.sql.Timestamp getTimestamp(int arg0)
			throws java.sql.SQLException {
		return wrappedObject.getTimestamp(arg0);
	}

	public java.sql.Timestamp getTimestamp(java.lang.String arg0)
			throws java.sql.SQLException {
		return wrappedObject.getTimestamp(arg0);
	}

	public java.sql.Timestamp getTimestamp(int arg0, java.util.Calendar arg1)
			throws java.sql.SQLException {
		return wrappedObject.getTimestamp(arg0, arg1);
	}

	public java.sql.SQLWarning getWarnings() throws java.sql.SQLException {
		return wrappedObject.getWarnings();
	}

	public void insertRow() throws java.sql.SQLException {
		wrappedObject.insertRow();
	}

	public boolean isAfterLast() throws java.sql.SQLException {
		return wrappedObject.isAfterLast();
	}

	public boolean isClosed() throws java.sql.SQLException {
		return wrappedObject.isClosed();
	}

	public boolean isFirst() throws java.sql.SQLException {
		return wrappedObject.isFirst();
	}

	public boolean isLast() throws java.sql.SQLException {
		return wrappedObject.isLast();
	}

	public boolean last() throws java.sql.SQLException {
		return wrappedObject.last();
	}

	public void refreshRow() throws java.sql.SQLException {
		wrappedObject.refreshRow();
	}

	public boolean relative(int arg0) throws java.sql.SQLException {
		return wrappedObject.relative(arg0);
	}

	public boolean rowDeleted() throws java.sql.SQLException {
		return wrappedObject.rowDeleted();
	}

	public boolean rowInserted() throws java.sql.SQLException {
		return wrappedObject.rowInserted();
	}

	public boolean rowUpdated() throws java.sql.SQLException {
		return wrappedObject.rowUpdated();
	}

	public void setFetchSize(int arg0) throws java.sql.SQLException {
		wrappedObject.setFetchSize(arg0);
	}

	public void updateArray(int arg0, java.sql.Array arg1)
			throws java.sql.SQLException {
		wrappedObject.updateArray(arg0, arg1);
	}

	public void updateArray(java.lang.String arg0, java.sql.Array arg1)
			throws java.sql.SQLException {
		wrappedObject.updateArray(arg0, arg1);
	}

	public void updateBlob(java.lang.String arg0, java.io.InputStream arg1)
			throws java.sql.SQLException {
		wrappedObject.updateBlob(arg0, arg1);
	}

	public void updateBlob(java.lang.String arg0, java.io.InputStream arg1,
			long arg2) throws java.sql.SQLException {
		wrappedObject.updateBlob(arg0, arg1, arg2);
	}

	public void updateBlob(int arg0, java.io.InputStream arg1)
			throws java.sql.SQLException {
		wrappedObject.updateBlob(arg0, arg1);
	}

	public void updateBlob(int arg0, java.io.InputStream arg1, long arg2)
			throws java.sql.SQLException {
		wrappedObject.updateBlob(arg0, arg1, arg2);
	}

	public void updateBlob(int arg0, java.sql.Blob arg1)
			throws java.sql.SQLException {
		wrappedObject.updateBlob(arg0, arg1);
	}

	public void updateBlob(java.lang.String arg0, java.sql.Blob arg1)
			throws java.sql.SQLException {
		wrappedObject.updateBlob(arg0, arg1);
	}

	public void updateByte(int arg0, byte arg1) throws java.sql.SQLException {
		wrappedObject.updateByte(arg0, arg1);
	}

	public void updateByte(java.lang.String arg0, byte arg1)
			throws java.sql.SQLException {
		wrappedObject.updateByte(arg0, arg1);
	}

	public void updateBytes(java.lang.String arg0, byte[] arg1)
			throws java.sql.SQLException {
		wrappedObject.updateBytes(arg0, arg1);
	}

	public void updateBytes(int arg0, byte[] arg1) throws java.sql.SQLException {
		wrappedObject.updateBytes(arg0, arg1);
	}

	public void updateClob(java.lang.String arg0, java.sql.Clob arg1)
			throws java.sql.SQLException {
		wrappedObject.updateClob(arg0, arg1);
	}

	public void updateClob(int arg0, java.sql.Clob arg1)
			throws java.sql.SQLException {
		wrappedObject.updateClob(arg0, arg1);
	}

	public void updateClob(java.lang.String arg0, java.io.Reader arg1)
			throws java.sql.SQLException {
		wrappedObject.updateClob(arg0, arg1);
	}

	public void updateClob(int arg0, java.io.Reader arg1)
			throws java.sql.SQLException {
		wrappedObject.updateClob(arg0, arg1);
	}

	public void updateClob(java.lang.String arg0, java.io.Reader arg1, long arg2)
			throws java.sql.SQLException {
		wrappedObject.updateClob(arg0, arg1, arg2);
	}

	public void updateClob(int arg0, java.io.Reader arg1, long arg2)
			throws java.sql.SQLException {
		wrappedObject.updateClob(arg0, arg1, arg2);
	}

	public void updateDate(java.lang.String arg0, java.sql.Date arg1)
			throws java.sql.SQLException {
		wrappedObject.updateDate(arg0, arg1);
	}

	public void updateDate(int arg0, java.sql.Date arg1)
			throws java.sql.SQLException {
		wrappedObject.updateDate(arg0, arg1);
	}

	public void updateDouble(int arg0, double arg1)
			throws java.sql.SQLException {
		wrappedObject.updateDouble(arg0, arg1);
	}

	public void updateDouble(java.lang.String arg0, double arg1)
			throws java.sql.SQLException {
		wrappedObject.updateDouble(arg0, arg1);
	}

	public void updateFloat(java.lang.String arg0, float arg1)
			throws java.sql.SQLException {
		wrappedObject.updateFloat(arg0, arg1);
	}

	public void updateFloat(int arg0, float arg1) throws java.sql.SQLException {
		wrappedObject.updateFloat(arg0, arg1);
	}

	public void updateInt(int arg0, int arg1) throws java.sql.SQLException {
		wrappedObject.updateInt(arg0, arg1);
	}

	public void updateInt(java.lang.String arg0, int arg1)
			throws java.sql.SQLException {
		wrappedObject.updateInt(arg0, arg1);
	}

	public void updateLong(java.lang.String arg0, long arg1)
			throws java.sql.SQLException {
		wrappedObject.updateLong(arg0, arg1);
	}

	public void updateLong(int arg0, long arg1) throws java.sql.SQLException {
		wrappedObject.updateLong(arg0, arg1);
	}

	public void updateNClob(int arg0, java.io.Reader arg1, long arg2)
			throws java.sql.SQLException {
		wrappedObject.updateNClob(arg0, arg1, arg2);
	}

	public void updateNClob(java.lang.String arg0, java.io.Reader arg1,
			long arg2) throws java.sql.SQLException {
		wrappedObject.updateNClob(arg0, arg1, arg2);
	}

	public void updateNClob(java.lang.String arg0, java.sql.NClob arg1)
			throws java.sql.SQLException {
		wrappedObject.updateNClob(arg0, arg1);
	}

	public void updateNClob(int arg0, java.io.Reader arg1)
			throws java.sql.SQLException {
		wrappedObject.updateNClob(arg0, arg1);
	}

	public void updateNClob(java.lang.String arg0, java.io.Reader arg1)
			throws java.sql.SQLException {
		wrappedObject.updateNClob(arg0, arg1);
	}

	public void updateNClob(int arg0, java.sql.NClob arg1)
			throws java.sql.SQLException {
		wrappedObject.updateNClob(arg0, arg1);
	}

	public void updateNull(java.lang.String arg0) throws java.sql.SQLException {
		wrappedObject.updateNull(arg0);
	}

	public void updateNull(int arg0) throws java.sql.SQLException {
		wrappedObject.updateNull(arg0);
	}

	public void updateObject(int arg0, java.lang.Object arg1, int arg2)
			throws java.sql.SQLException {
		wrappedObject.updateObject(arg0, arg1, arg2);
	}

	public void updateObject(int arg0, java.lang.Object arg1)
			throws java.sql.SQLException {
		wrappedObject.updateObject(arg0, arg1);
	}

	public void updateObject(java.lang.String arg0, java.lang.Object arg1)
			throws java.sql.SQLException {
		wrappedObject.updateObject(arg0, arg1);
	}

	public void updateObject(java.lang.String arg0, java.lang.Object arg1,
			int arg2) throws java.sql.SQLException {
		wrappedObject.updateObject(arg0, arg1, arg2);
	}

	public void updateRef(int arg0, java.sql.Ref arg1)
			throws java.sql.SQLException {
		wrappedObject.updateRef(arg0, arg1);
	}

	public void updateRef(java.lang.String arg0, java.sql.Ref arg1)
			throws java.sql.SQLException {
		wrappedObject.updateRef(arg0, arg1);
	}

	public void updateRow() throws java.sql.SQLException {
		wrappedObject.updateRow();
	}

	public void updateRowId(int arg0, java.sql.RowId arg1)
			throws java.sql.SQLException {
		wrappedObject.updateRowId(arg0, arg1);
	}

	public void updateRowId(java.lang.String arg0, java.sql.RowId arg1)
			throws java.sql.SQLException {
		wrappedObject.updateRowId(arg0, arg1);
	}

	public void updateSQLXML(java.lang.String arg0, java.sql.SQLXML arg1)
			throws java.sql.SQLException {
		wrappedObject.updateSQLXML(arg0, arg1);
	}

	public void updateSQLXML(int arg0, java.sql.SQLXML arg1)
			throws java.sql.SQLException {
		wrappedObject.updateSQLXML(arg0, arg1);
	}

	public void updateShort(int arg0, short arg1) throws java.sql.SQLException {
		wrappedObject.updateShort(arg0, arg1);
	}

	public void updateShort(java.lang.String arg0, short arg1)
			throws java.sql.SQLException {
		wrappedObject.updateShort(arg0, arg1);
	}

	public void updateString(int arg0, java.lang.String arg1)
			throws java.sql.SQLException {
		wrappedObject.updateString(arg0, arg1);
	}

	public void updateString(java.lang.String arg0, java.lang.String arg1)
			throws java.sql.SQLException {
		wrappedObject.updateString(arg0, arg1);
	}

	public void updateTime(int arg0, java.sql.Time arg1)
			throws java.sql.SQLException {
		wrappedObject.updateTime(arg0, arg1);
	}

	public void updateTime(java.lang.String arg0, java.sql.Time arg1)
			throws java.sql.SQLException {
		wrappedObject.updateTime(arg0, arg1);
	}

	public boolean wasNull() throws java.sql.SQLException {
		return wrappedObject.wasNull();
	}

	public <T> T unwrap(java.lang.Class<T> arg0) throws java.sql.SQLException {
		return wrappedObject.unwrap(arg0);
	}

	public boolean isWrapperFor(java.lang.Class<?> arg0)
			throws java.sql.SQLException {
		return wrappedObject.isWrapperFor(arg0);
	}

}
