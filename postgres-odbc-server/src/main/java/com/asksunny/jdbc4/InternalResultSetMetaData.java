package com.asksunny.jdbc4;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InternalResultSetMetaData implements ResultSetMetaData {

	private List<InternalColumnMetaData> columnMetaDatas = null;

	public InternalResultSetMetaData() {
		this(null);
	}

	public InternalResultSetMetaData(
			List<InternalColumnMetaData> columnMetaDatas) {
		if (columnMetaDatas != null) {
			this.columnMetaDatas = columnMetaDatas;
		} else {
			this.columnMetaDatas = new ArrayList<InternalColumnMetaData>();
		}

	}

	public void addColumnMetaData(String columnName, int jdbcType,
			int displaySize) {
		InternalColumnMetaData md = new InternalColumnMetaData(columnName,
				jdbcType, displaySize);
		columnMetaDatas.add(md);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return iface.cast(this);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return iface.isInstance(this);
	}

	@Override
	public int getColumnCount() throws SQLException {		
		return columnMetaDatas.size();
	}
	
	
	

	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isSearchable(int column) throws SQLException {	
		return false;
	}

	@Override
	public boolean isCurrency(int column) throws SQLException {		
		return false;
	}

	@Override
	public int isNullable(int column) throws SQLException {		
		return 0;
	}

	@Override
	public boolean isSigned(int column) throws SQLException {		
		return false;
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		if(column<1 || column>getColumnCount()){
			throw new IndexOutOfBoundsException(String.format("Column Index %d is out of bound %d", column, getColumnCount()));
		}
		InternalColumnMetaData cmd = this.columnMetaDatas.get(column-1);		
		return cmd.getDisplaySize();
	}

	@Override
	public String getColumnLabel(int column) throws SQLException {
		if(column<1 || column>getColumnCount()){
			throw new IndexOutOfBoundsException(String.format("Column Index %d is out of bound %d", column, getColumnCount()));
		}
		InternalColumnMetaData cmd = this.columnMetaDatas.get(column-1);	
		return cmd.getName();
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		if(column<1 || column>getColumnCount()){
			throw new IndexOutOfBoundsException(String.format("Column Index %d is out of bound %d", column, getColumnCount()));
		}
		InternalColumnMetaData cmd = this.columnMetaDatas.get(column-1);	
		return cmd.getName();
	}

	@Override
	public String getSchemaName(int column) throws SQLException {		
		return null;
	}

	@Override
	public int getPrecision(int column) throws SQLException {		
		return 0;
	}

	@Override
	public int getScale(int column) throws SQLException {		
		return 0;
	}

	@Override
	public String getTableName(int column) throws SQLException {		
		return null;
	}

	@Override
	public String getCatalogName(int column) throws SQLException {		
		return null;
	}

	@Override
	public int getColumnType(int column) throws SQLException {		
		return 0;
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {		
		return null;
	}

	@Override
	public boolean isReadOnly(int column) throws SQLException {		
		return false;
	}

	@Override
	public boolean isWritable(int column) throws SQLException {		
		return false;
	}

	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {		
		return false;
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {		
		return null;
	}

}
