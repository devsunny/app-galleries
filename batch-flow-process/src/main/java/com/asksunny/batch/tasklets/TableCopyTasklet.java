package com.asksunny.batch.tasklets;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.batch.FatalExecutionException;

public class TableCopyTasklet extends AbstractTasklet {
	private static Logger logger = LoggerFactory.getLogger(TableCopyTasklet.class);
	private SqlSessionFactory sourceSqlSessionFactory;
	private SqlSessionFactory destinationSqlSessionFactory;
	private String sourceSelectSqlId;
	private String destinationInsertSqlId;
	

	public TableCopyTasklet() {

	}

	public String[] execute() {
		try {
			SqlSession session = sourceSqlSessionFactory.openSession();
			SqlSession session2 = destinationSqlSessionFactory.openSession(ExecutorType.BATCH);
			session.select(sourceSelectSqlId, new TableCopyResultSetHandler(session2, destinationInsertSqlId));
			session2.flushStatements();
		} catch (Exception e) {
			logger.error("Failed to execute task", e);
			if (failedTaskIds != null && failedTaskIds.length > 0) {
				return failedTaskIds;
			} else {
				throw new FatalExecutionException("Failed to execute task", e);
			}
		}
		return successTaskIds;
	}

	public SqlSessionFactory getSourceSqlSessionFactory() {
		return sourceSqlSessionFactory;
	}

	public void setSourceSqlSessionFactory(SqlSessionFactory sourceSqlSessionFactory) {
		this.sourceSqlSessionFactory = sourceSqlSessionFactory;
	}

	public SqlSessionFactory getDestinationSqlSessionFactory() {
		return destinationSqlSessionFactory;
	}

	public void setDestinationSqlSessionFactory(SqlSessionFactory destinationSqlSessionFactory) {
		this.destinationSqlSessionFactory = destinationSqlSessionFactory;
	}

	public String getSourceSelectSqlId() {
		return sourceSelectSqlId;
	}

	public void setSourceSelectSqlId(String sourceSelectSqlId) {
		this.sourceSelectSqlId = sourceSelectSqlId;
	}

	public String getDestinationInsertSqlId() {
		return destinationInsertSqlId;
	}

	public void setDestinationInsertSqlId(String destinationInsertSqlId) {
		this.destinationInsertSqlId = destinationInsertSqlId;
	}

	
}
