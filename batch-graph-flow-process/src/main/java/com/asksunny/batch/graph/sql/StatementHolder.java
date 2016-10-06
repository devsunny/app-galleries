package com.asksunny.batch.graph.sql;

import com.asksunny.batch.graph.FlowTaskParameterType;

public class StatementHolder {

	private int batchSize;
	private boolean autoCommit;
	private String sqlSource;
	protected FlowTaskParameterType statementParameterType = FlowTaskParameterType.None;
	protected String statementParameterName = null;
	private String name;
	
	
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


	public FlowTaskParameterType getStatementParameterType() {
		return statementParameterType;
	}


	public void setStatementParameterType(FlowTaskParameterType statementParameterType) {
		this.statementParameterType = statementParameterType;
	}


	public String getStatementParameterName() {
		return statementParameterName;
	}


	public void setStatementParameterName(String statementParameterName) {
		this.statementParameterName = statementParameterName;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	
}
