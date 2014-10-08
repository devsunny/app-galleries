package com.asksunny.db;

import java.sql.SQLException;

public interface ExtendSqlFunction {
	public final static String EXTEND_SQL_FUNCTION_SQLSTATE = "ExSF9999";
	
	Object invoke(Object[] params) throws SQLException;
	String getName();
}
