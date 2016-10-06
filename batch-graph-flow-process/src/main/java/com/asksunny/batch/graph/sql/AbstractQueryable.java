package com.asksunny.batch.graph.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.asksunny.batch.graph.BatchFlowContext;
import com.asksunny.batch.graph.FlowTaskParameterType;
import com.asksunny.batch.graph.TextPreprocessor;

public abstract class AbstractQueryable {

	private static final Logger logger = LoggerFactory.getLogger(AbstractQueryable.class);
	private DataSource datasource;
	private StatementHolder statement;
	private TextPreprocessor statementPreprocessor;
	protected BatchFlowContext flowContext;

	private String taskName;

	protected PreparedStatement prepareStatement(Connection conn, StatementHolder sourceStatement) throws Exception {
		Object params = getParameter(sourceStatement.getStatementParameterType(),
				sourceStatement.getStatementParameterName());
		PreparedStatement pstmt = null;
		String sql = sourceStatement.getSqlSource();
		if (getStatementPreprocessor() != null) {
			sql = getStatementPreprocessor().preprocess(sourceStatement.getSqlSource(), params);
		}
		pstmt = conn.prepareStatement(sql);
		return pstmt;
	}

	protected void setParameters(PreparedStatement pstmt, StatementHolder sourceStatement) throws Exception {
		Object params = getParameter(sourceStatement.getStatementParameterType(),
				sourceStatement.getStatementParameterName());
		setParameters(pstmt, params, sourceStatement);
	}

	@SuppressWarnings("rawtypes")
	protected void setParameters(PreparedStatement pstmt, Object params, StatementHolder sourceStatement)
			throws Exception {
		if (params instanceof Properties) {
			setParam(pstmt, (Properties) params, sourceStatement);
		} else if (params instanceof List) {
			setParam(pstmt, (List) params, sourceStatement);
		} else if (params instanceof Map) {
			setParam(pstmt, (Map) params, sourceStatement);
		} else if (params instanceof ResultSet) {
			setParam(pstmt, (ResultSet) params, sourceStatement);
		} else {
			setParam(pstmt, params, sourceStatement);
		}
	}

	protected void setParam(PreparedStatement pstmt, List<?> params, StatementHolder sourceStatement) throws Exception {
		StatementParameter[] stmtParams = sourceStatement.getParameters();
		if (params == null || stmtParams == null || stmtParams.length == 0) {
			logger.debug("No parameter found for statment:{}", sourceStatement.getName());
			return;
		}
		for (int i = 0; i < stmtParams.length; i++) {
			StatementParameter sp = stmtParams[i];
			Object param = params.get(sp.getResultIndex() - 1);
			if (param == null) {
				pstmt.setNull(sp.getParamIndex(), sp.getJdbcType());
			} else {
				pstmt.setObject(sp.getParamIndex(), param, sp.getJdbcType());
			}

		}

	}

	protected void setParam(PreparedStatement pstmt, Map<?, ?> params, StatementHolder sourceStatement)
			throws Exception {
		StatementParameter[] stmtParams = sourceStatement.getParameters();
		if (params == null || stmtParams == null || stmtParams.length == 0) {
			logger.debug("No parameter found for statment:{}", sourceStatement.getName());
			return;
		}
		for (int i = 0; i < stmtParams.length; i++) {
			StatementParameter sp = stmtParams[i];
			Object param = params.get(sp.getName());
			if (param == null) {
				pstmt.setNull(sp.getParamIndex(), sp.getJdbcType());
			} else {
				pstmt.setObject(sp.getParamIndex(), param, sp.getJdbcType());
			}

		}
	}

	protected void setParam(PreparedStatement pstmt, ResultSet params, StatementHolder sourceStatement)
			throws Exception {
		StatementParameter[] stmtParams = sourceStatement.getParameters();
		if (params == null || stmtParams == null || stmtParams.length == 0) {
			logger.debug("No parameter found for statment:{}", sourceStatement.getName());
			return;
		}
		for (int i = 0; i < stmtParams.length; i++) {
			StatementParameter sp = stmtParams[i];
			Object param = params.getObject(sp.getResultIndex());
			if (param == null) {
				pstmt.setNull(sp.getParamIndex(), sp.getJdbcType());
			} else {
				pstmt.setObject(sp.getParamIndex(), param, sp.getJdbcType());
			}

		}
	}

	protected void setParam(PreparedStatement pstmt, Properties params, StatementHolder sourceStatement)
			throws Exception {
		StatementParameter[] stmtParams = sourceStatement.getParameters();
		if (params == null || stmtParams == null || stmtParams.length == 0) {
			logger.debug("No parameter found for statment:{}", sourceStatement.getName());
			return;
		}
		for (int i = 0; i < stmtParams.length; i++) {
			StatementParameter sp = stmtParams[i];
			String param = params.getProperty(sp.getName());
			if (param == null) {
				pstmt.setNull(sp.getParamIndex(), sp.getJdbcType());
			} else {
				pstmt.setObject(sp.getParamIndex(), param, sp.getJdbcType());
			}

		}
	}

	protected void setParam(PreparedStatement pstmt, Object param, StatementHolder sourceStatement) throws Exception {
		StatementParameter[] stmtParams = sourceStatement.getParameters();
		for (int i = 0; i < stmtParams.length; i++) {
			StatementParameter sp = stmtParams[i];
			if (param == null) {
				pstmt.setNull(sp.getParamIndex(), sp.getJdbcType());
			} else {
				pstmt.setObject(sp.getParamIndex(), param, sp.getJdbcType());
			}

		}
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

	public void setStatementPreprocessor(TextPreprocessor statementPreprocessor) {
		this.statementPreprocessor = statementPreprocessor;
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	public StatementHolder getStatement() {
		return statement;
	}

	public void setStatement(StatementHolder statement) {
		this.statement = statement;
	}

	public BatchFlowContext getFlowContext() {
		return flowContext;
	}

	public void setFlowContext(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Object getParameter(FlowTaskParameterType pType, String parameterName) {
		switch (pType) {
		case CLIArgumentContext:
			return getFlowContext().getCliArgument();
		case BatchFlowContext:
			return getFlowContext();
		case CLIArgument:
			return getFlowContext().getCliArgument().get(parameterName);
		case BatchFlowContextObject:
			return getFlowContext().get(parameterName);
		case SystemProperties:
			return System.getProperties();
		case SystemEnvs:
			return System.getenv();
		case None:
			return null;
		default:
			return null;
		}
	}

	public TextPreprocessor getStatementPreprocessor() {
		return statementPreprocessor;
	}
}
