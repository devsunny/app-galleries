package com.asksunny.batch.graph.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.asksunny.batch.graph.AbstractWorkflowTask;
import com.asksunny.batch.graph.TextPreprocessor;

public abstract class AbstractSQLWorkflowTask extends AbstractWorkflowTask {
	private static final Logger logger = LoggerFactory.getLogger(AbstractSQLWorkflowTask.class);
	private DataSource sourceDataSource;
	private StatementHolder sourceStatement;
	private TextPreprocessor statementPreprocessor;

	public AbstractSQLWorkflowTask() {
		super();
	}

	
	
	
	
	
	protected PreparedStatement prepareStatement(Connection conn, StatementHolder sourceStatement) throws Exception {
		Object params = getTaskParameter();
		PreparedStatement pstmt = null;
		String sql = sourceStatement.getSqlSource();
		if (getStatementPreprocessor() != null) {
			sql = getStatementPreprocessor().preprocess(sourceStatement.getSqlSource(), params);
		}
		pstmt = conn.prepareStatement(sql);
		return pstmt;

	}

	protected void close(ResultSet rs) {
		try {
			rs.close();
		} catch (SQLException e) {
			;
		}
	}

	protected void close(Statement stmt) {
		try {
			stmt.close();
		} catch (SQLException e) {
			;
		}
	}

	protected void close(Connection conn) {
		close(null, conn);
	}

	protected void close(DataSource ds, Connection conn) {
		if (ds == null || (ds != null && ds instanceof SingleConnectionDataSource
				&& ((SingleConnectionDataSource) ds).shouldClose(conn))) {
			try {
				conn.close();
			} catch (SQLException e) {
				;
			}
		} else {
			logger.info("Single Connection DataSource, leave connection open util JVM destroyed");
		}

	}

	public DataSource getSourceDataSource() {
		return sourceDataSource;
	}

	public void setSourceDataSource(DataSource sourceDataSource) {
		this.sourceDataSource = sourceDataSource;
	}

	public StatementHolder getSourceStatement() {
		return sourceStatement;
	}

	public void setSourceStatement(StatementHolder sourceStatement) {
		this.sourceStatement = sourceStatement;
	}

	public TextPreprocessor getStatementPreprocessor() {
		return statementPreprocessor;
	}

	public void setStatementPreprocessor(TextPreprocessor statementPreprocessor) {
		this.statementPreprocessor = statementPreprocessor;
	}

}
