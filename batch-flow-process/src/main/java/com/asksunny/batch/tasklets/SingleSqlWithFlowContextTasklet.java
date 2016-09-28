package com.asksunny.batch.tasklets;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.batch.FatalExecutionException;

public class SingleSqlWithFlowContextTasklet extends AbstractTasklet {
	private static Logger logger = LoggerFactory.getLogger(SingleSqlWithFlowContextTasklet.class);
	private SqlSessionFactory sqlSessionFactory;
	private String sqlId;
	private String flowContextParamName;

	public SingleSqlWithFlowContextTasklet() {
	}

	public String[] execute() {
		if (logger.isDebugEnabled()) {
			logger.debug("Executing SQL:{}", sqlId);
		}
		try {
			SqlSession session = sqlSessionFactory.openSession(true);
			if (flowContextParamName == null) {
				session.update(sqlId);
			} else {
				session.update(sqlId, getFlowContext().get(flowContextParamName));
			}			
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

	public String getFlowContextParamName() {
		return flowContextParamName;
	}

	public void setFlowContextParamName(String flowContextParamName) {
		this.flowContextParamName = flowContextParamName;
	}

}
