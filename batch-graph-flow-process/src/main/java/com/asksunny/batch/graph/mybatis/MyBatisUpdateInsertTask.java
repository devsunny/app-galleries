package com.asksunny.batch.graph.mybatis;

import java.util.Collection;
import java.util.Iterator;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.batch.graph.AbstractWorkflowTask;
import com.asksunny.batch.graph.FlowTaskParameterType;

public class MyBatisUpdateInsertTask extends AbstractWorkflowTask {
	private static final Logger logger = LoggerFactory.getLogger(MyBatisUpdateInsertTask.class);
	private SqlSessionFactory sqlSessionFactory;
	private String insertSqlId;
	private String updateSqlId;
	private UpdateInsertMode mode = UpdateInsertMode.INSERT;
	private String parameterName;
	private FlowTaskParameterType parameterType = FlowTaskParameterType.BatchFlowContextObject;
	private boolean autoCommit = Boolean.FALSE;
	private boolean ignoreNull = Boolean.FALSE;

	public MyBatisUpdateInsertTask() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void executeTask() throws Exception {

		Object param = FlowTaskParameterType.getParameter(getFlowContext(), getParameterType(), getParameterName());
		if (param == null && ignoreNull) {
			String msg = String.format("Could locate the parameter %s in the scope of ", getParameterName(),
					getParameterType());
			logger.info("WARN:{}", msg);
			return;
		} else if (param == null && !isIgnoreNull()) {
			String msg = String.format("Could locate the parameter %s in the scope of ", getParameterName(),
					getParameterType());
			logger.error("Fatal:{}", msg);
			throw new Exception(msg);
		}
		SqlSession session = sqlSessionFactory.openSession(isAutoCommit());
		int effectedRows = 0;
		try {
			if (param instanceof Collection) {
				Collection cls = (Collection) param;
				for (Iterator iterator = cls.iterator(); iterator.hasNext();) {
					Object object = (Object) iterator.next();
					effectedRows += updateInsert(session, object);
				}
			} else {
				effectedRows += updateInsert(session, param);

			}
			session.commit();
			logger.info("Effected rows:{}", effectedRows);
		} catch (Exception ex) {
			session.rollback();
			logger.error("Failed to update and rollback", ex);
			throw ex;
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	protected int updateInsert(SqlSession session, Object obj) throws Exception {
		int c = 0;
		switch (getMode()) {
		case UPDATE:
			c = session.update(getUpdateSqlId(), obj);
			break;
		case UPSERT:
			c = session.update(getUpdateSqlId(), obj);
			if (c <= 0) {
				session.insert(getInsertSqlId(), obj);
			}
			break;
		default:
			c = session.insert(getInsertSqlId(), obj);
			break;
		}
		return c;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public String getInsertSqlId() {
		return insertSqlId;
	}

	public void setInsertSqlId(String insertSqlId) {
		this.insertSqlId = insertSqlId;
	}

	public String getUpdateSqlId() {
		return updateSqlId;
	}

	public void setUpdateSqlId(String updateSqlId) {
		this.updateSqlId = updateSqlId;
	}

	public UpdateInsertMode getMode() {
		return mode;
	}

	public void setMode(UpdateInsertMode mode) {
		this.mode = mode;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public FlowTaskParameterType getParameterType() {
		return parameterType;
	}

	public void setParameterType(FlowTaskParameterType parameterType) {
		this.parameterType = parameterType;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public boolean isIgnoreNull() {
		return ignoreNull;
	}

	public void setIgnoreNull(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}


}
