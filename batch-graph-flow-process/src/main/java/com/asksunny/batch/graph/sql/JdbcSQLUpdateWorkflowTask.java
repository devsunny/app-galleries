package com.asksunny.batch.graph.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.batch.graph.FlowTaskParameterType;

public class JdbcSQLUpdateWorkflowTask extends AbstractSQLWorkflowTask {
	private static final Logger logger = LoggerFactory.getLogger(JdbcSQLUpdateWorkflowTask.class);
	private List<StatementHolder> statements;
	private boolean autoCommit = false;

	public JdbcSQLUpdateWorkflowTask() {

	}

	@Override
	protected void executeTask() throws Exception {
		Connection connection = getDatasource().getConnection();
		connection.setAutoCommit(isAutoCommit());
		try {
			for (StatementHolder statement : statements) {
				Object params = FlowTaskParameterType.getParameter(getFlowContext(),
						statement.getStatementParameterType(), statement.getStatementParameterName());
				PreparedStatement pstmt = prepareStatement(connection, statement);
				if (statement.getParameters() != null && statement.getParameters().length > 0) {
					setParameters(pstmt, params, statement);
				}
				int c = pstmt.executeUpdate();
				logger.info("{} statement executed with effected rows [{}]", statement.getName(), c);
			}
			connection.commit();
		} catch (Exception ex) {
			if (!autoCommit) {
				connection.rollback();
			}
		}

	}

	public List<StatementHolder> getStatements() {
		return statements;
	}

	public void setStatements(List<StatementHolder> statements) {
		this.statements = statements;
	}

	public void setStatements(StatementHolder[] statements) {
		this.statements = Arrays.asList(statements);
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

}
