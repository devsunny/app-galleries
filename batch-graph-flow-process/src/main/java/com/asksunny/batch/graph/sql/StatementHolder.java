package com.asksunny.batch.graph.sql;

public class StatementHolder {

	private int batchSize;
	private boolean autoCommit;
	private String sqlSource;
	private StatementParameter[] parameters = StatementParameter.EMPTY_PARAMS;

	public StatementHolder() {
	}
	

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public String getSqlSource() {
		return sqlSource;
	}

	public void setSqlSource(String sqlSource) {
		this.sqlSource = sqlSource;
	}

	public StatementParameter[] getParameters() {
		return parameters;
	}

	public void setParameters(StatementParameter[] parameters) {
		this.parameters = parameters;
	}

}
