package com.asksunny.batch.graph.sql;

public class StatementParameter {

	public static final StatementParameter[] EMPTY_PARAMS = new StatementParameter[0];
	
	private String name;
	private int resultIndex;
	private int paramIndex;
	private int jdbcType;

	public StatementParameter() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getResultIndex() {
		return resultIndex;
	}

	public void setResultIndex(int resultIndex) {
		this.resultIndex = resultIndex;
	}

	public int getParamIndex() {
		return paramIndex;
	}

	public void setParamIndex(int paramIndex) {
		this.paramIndex = paramIndex;
	}

	public int getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}

	public void setJdbcTypeName(String jdbcTypeName) {
		this.jdbcType = JdbcTypeMap.getType(jdbcTypeName);
	}

	public String getJdbcTypeName() {
		return JdbcTypeMap.getTypeName(this.jdbcType);
	}

}
