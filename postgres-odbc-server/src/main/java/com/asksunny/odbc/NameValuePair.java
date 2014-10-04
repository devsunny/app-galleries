package com.asksunny.odbc;

public class NameValuePair {

	private String name;
	private String value;

	public NameValuePair() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

	}

	public String getValue() {
		return value==null?"":value;
	}

	public void setValue(String value) {
		this.value = value;

	}

	public NameValuePair(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

}
