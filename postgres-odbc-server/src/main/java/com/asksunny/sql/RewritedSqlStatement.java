package com.asksunny.sql;

import net.sf.jsqlparser.statement.Statement;

import com.asksunny.odbc.SQLCommandType;

public class RewritedSqlStatement {
	private String sql;
	private String rewritedsql;
	private SQLCommandType type;
	private Statement parsedStatement;

	public String getSql() {
		return sql;
	}

	public boolean wasRewrited() {
		return (this.rewritedsql != null || this.parsedStatement != null);
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getRewritedsql() {

		if (this.rewritedsql != null) {
			return this.rewritedsql;
		} else if (parsedStatement != null) {
			return this.parsedStatement.toString();
		} else {
			return sql;
		}
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

	public Statement getParsedStatement() {
		return parsedStatement;
	}

	public void setParsedStatement(Statement parsedStatement) {
		this.parsedStatement = parsedStatement;
	}

}
