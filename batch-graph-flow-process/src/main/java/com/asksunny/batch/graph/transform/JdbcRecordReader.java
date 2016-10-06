package com.asksunny.batch.graph.transform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

public class JdbcRecordReader implements RecordReader {
	private static final Logger logger = LoggerFactory.getLogger(JdbcRecordReader.class);
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

	@Override
	public boolean next() throws Exception {
		if (initialized.weakCompareAndSet(false, true)) {
			connection = getSourceDataSource().getConnection();
			preparedStatement = prepareStatement(connection, getSourceStatement());
			resultSet = preparedStatement.executeQuery();
			columnCount = resultSet.getMetaData().getColumnCount();
			record = new ArrayList<>(columnCount);
		}
		if (resultSet.isClosed() && resultSet == null) {
			return false;
		}

		if (!resultSet.next()) {
			close();
			return false;
		} else {
			return true;
		}

	}

	@Override
	public Object getNext() throws Exception {
		record.clear();
		for (int i = 0; i < columnCount; i++) {
			record.add(resultSet.getObject(i + 1));
		}
		return record;
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

	public JdbcRecordReader() {

	}

	@Override
	public void init(BatchFlowContext flowContext) {
		setFlowContext(flowContext);

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
	public void close() {
		// TODO Auto-generated method stub

	}

}
