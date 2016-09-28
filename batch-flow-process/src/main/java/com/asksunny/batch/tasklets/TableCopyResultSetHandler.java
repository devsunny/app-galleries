package com.asksunny.batch.tasklets;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;

public class TableCopyResultSetHandler implements ResultHandler<Object> {
	SqlSession session;
	String insertId;
	long updatedCount = 0;

	public TableCopyResultSetHandler(SqlSession session2, String insertId) {
		this.session = session2;
		this.insertId = insertId;
		updatedCount = 0;
	}

	@Override
	public void handleResult(ResultContext<? extends Object> resultContext) {
		Object obj = resultContext.getResultObject();
		session.insert(insertId, obj);
		if (updatedCount % 250 == 0) {
			session.flushStatements();
		}
	}

	public SqlSession getSession() {
		return session;
	}

	public void setSession(SqlSession session) {
		this.session = session;
	}

	public String getInsertId() {
		return insertId;
	}

	public void setInsertId(String insertId) {
		this.insertId = insertId;
	}

}
