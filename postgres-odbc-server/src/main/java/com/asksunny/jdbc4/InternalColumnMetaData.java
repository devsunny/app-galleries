package com.asksunny.jdbc4;

public class InternalColumnMetaData {

	private String name;
	private int displaySize = 0;
	private int jdbcType;

	public InternalColumnMetaData() {

	}

	public InternalColumnMetaData(String name, int jdbcType, int displaySize) {
		super();
		this.name = name;
		this.jdbcType = jdbcType;
		this.displaySize = displaySize;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

	}

	public int getDisplaySize() {
		return displaySize;
	}

	public void setDisplaySize(int displaySize) {
		this.displaySize = displaySize;

	}

	public int getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;

	}

}
