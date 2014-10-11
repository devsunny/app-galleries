package com.asksunny.sql;

import com.asksunny.odbc.SQLCommandType;

public class RewritedSqlStatement 
{
	private String sql;
	private String rewritedsql;
	private SQLCommandType type;
	
	
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getRewritedsql() {
		return rewritedsql;
	}
	public void setRewritedsql(String rewritedsql) {
		this.rewritedsql = rewritedsql;
	}
	public SQLCommandType getType() {
		return type;
	}
	public void setType(SQLCommandType type) {
		this.type = type;
	}
	
	
	
	
}
