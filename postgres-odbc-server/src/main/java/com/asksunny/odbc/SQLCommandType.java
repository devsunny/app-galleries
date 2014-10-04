package com.asksunny.odbc;

public enum SQLCommandType {

	INSERT, DELETE, UPDATE, SELECT, CALL, BEGIN, OTHER ;

	public static final int PG_QUERY_SET_CLIENT_PROP = 0;
	public static final int PG_QUERY_GET_CLIENT_PROP = 1;
	public static final int PG_QUERY_SELECT = 2;
	public static final int PG_QUERY_INSERT = 3;
	public static final int PG_QUERY_DELETE = 4;
	public static final int PG_QUERY_UPDATE = 5;
	public static final int PG_QUERY_OTHER = 6; // all other ddl commands;
	public static final int PG_QUERY_PARSER_ERROR = -1; // all other ddl
														// commands;

}
