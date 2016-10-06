package com.asksunny.batch.graph.transform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.asksunny.batch.graph.BatchFlowContext;
import com.asksunny.batch.graph.FlowTaskParameterType;
import com.asksunny.batch.graph.TextPreprocessor;
import com.asksunny.batch.graph.sql.StatementHolder;
import com.asksunny.batch.graph.sql.StatementParameter;

public class JdbcRecordStoreTransformer implements RecordTransformer {
	private static final Logger logger = LoggerFactory.getLogger(JdbcRecordStoreTransformer.class);
	private DataSource sourceDataSource;
	private StatementHolder sourceStatement;
	private TextPreprocessor statementPreprocessor;
	protected FlowTaskParameterType flowTaskParameterType = FlowTaskParameterType.None;
	protected BatchFlowContext flowContext;
	protected String parameterName = null;
	protected AtomicBoolean initialized = new AtomicBoolean(false);
	protected ResultSet resultSet = null;
	protected PreparedStatement preparedStatement = null;
	protected Connection connection = null;
	protected int columnCount = 1;
	protected List<Object> record = null;

	protected long updatedRecords = 0;
	protected long effectedRecords = 0;

	public JdbcRecordStoreTransformer() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object transform(Object transformee) throws Exception {
		updatedRecords++;
		List listParam = (List) transformee;
		setParameter(preparedStatement, listParam, sourceStatement);
		preparedStatement.addBatch();
		if (updatedRecords % sourceStatement.getBatchSize() == 0) {
			int[] effs = preparedStatement.executeBatch();
			for (int i = 0; i < effs.length; i++) {
				effectedRecords += effs[i];
			}
		}
		return transformee;
	}

	@SuppressWarnings("rawtypes")
	protected void setParameter(PreparedStatement pstmt, List listParam, StatementHolder sourceStatement)
			throws Exception {
		for (int i = 0; i < sourceStatement.getParameters().length; i++) {
			StatementParameter sp = sourceStatement.getParameters()[i];
			Object pp = listParam.get(sp.getResultIndex());
			if (pp == null) {
				pstmt.setNull(sp.getParamIndex(), sp.getJdbcType());
			} else {
				pstmt.setObject(sp.getParamIndex(), pp, sp.getJdbcType());
			}
		}
	}

	protected PreparedStatement prepareStatement(Connection conn, StatementHolder sourceStatement) throws Exception {
		Object params = getParameter();
		PreparedStatement pstmt = null;
		String sql = sourceStatement.getSqlSource();
		if (getStatementPreprocessor() != null) {
			sql = getStatementPreprocessor().preprocess(sourceStatement.getSqlSource(), params);
		}
		pstmt = conn.prepareStatement(sql);
		@SuppressWarnings("rawtypes")
		Map mapParam = (params instanceof Map) ? (Map) params : null;
		if (sourceStatement.getParameters() != null && sourceStatement.getParameters().length > 0) {
			for (int i = 0; i < sourceStatement.getParameters().length; i++) {
				StatementParameter sp = sourceStatement.getParameters()[i];
				if (mapParam != null) {
					Object pp = mapParam.get(sp.getName());
					if (pp == null) {
						pstmt.setNull(sp.getParamIndex(), sp.getJdbcType());
					} else {
						pstmt.setObject(sp.getParamIndex(), pp, sp.getJdbcType());
					}
				} else {
					if (params == null) {
						pstmt.setNull(sp.getParamIndex(), sp.getJdbcType());
					} else {
						pstmt.setObject(sp.getParamIndex(), params, sp.getJdbcType());
					}
				}
			}
		}
		return pstmt;

	}

	public Object getParameter() {
		switch (flowTaskParameterType) {
		case CLIArgumentContext:
			return getFlowContext().getCliArgument();
		case BatchFlowContext:
			return getFlowContext();
		case CLIArgument:
			return getFlowContext().getCliArgument().get(getParameterName());
		case BatchFlowContextObject:
			return getFlowContext().get(getParameterName());
		case None:
			return null;
		default:
			return null;
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

	@Override
	public void init(BatchFlowContext flowContext) {
		setFlowContext(flowContext);
		if (initialized.weakCompareAndSet(false, true)) {
			try {
				connection = getSourceDataSource().getConnection();
				connection.setAutoCommit(getSourceStatement().isAutoCommit());
				preparedStatement = prepareStatement(connection, getSourceStatement());
			} catch (Exception e) {
				throw new RuntimeException("Failed to prepare statement", e);
			}
		}
	}

	public FlowTaskParameterType getFlowTaskParameterType() {
		return flowTaskParameterType;
	}

	public void setFlowTaskParameterType(FlowTaskParameterType flowTaskParameterType) {
		this.flowTaskParameterType = flowTaskParameterType;
	}

	public BatchFlowContext getFlowContext() {
		return flowContext;
	}

	public void setFlowContext(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	@Override
	public void shutdown() throws Exception {
		if (updatedRecords % sourceStatement.getBatchSize() != 0) {
			int[] effs = preparedStatement.executeBatch();
			for (int i = 0; i < effs.length; i++) {
				effectedRecords += effs[i];
			}
		}
		logger.info("Updated count:[{}] effected count:[{}]", updatedRecords, effectedRecords);
		if (!getSourceStatement().isAutoCommit()) {
			connection.commit();
		}
		close(preparedStatement);
		close(getSourceDataSource(), connection);
	}

}
