package com.asksunny.batch.tasklets;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.batch.FatalExecutionException;

public class SingleSqlTasklet extends AbstractTasklet {
	private static Logger logger = LoggerFactory.getLogger(SingleSqlTasklet.class);
	private SqlSessionFactory sqlSessionFactory;
	private String sqlId;

	public SingleSqlTasklet() {
	}

	public String[] execute() {
		if (logger.isDebugEnabled()) {
			logger.debug("Executing SQL:{}", sqlId);
		}
		try {
			SqlSession session = sqlSessionFactory.openSession(true);
			session.update(sqlId);
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

	
	
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public String getSqlId() {
		return sqlId;
	}

	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}

}
