package com.asksunny.copybook.elements;

public class FieldDetail implements CopyBookElement{

	
	private DataLevel  dataLevel;
	private DataTypeDetail detail;
	private DataUsage usage;
	
	private String name;
	
	
	public FieldDetail() {
		// TODO Auto-generated constructor stub
	}


	public DataLevel getDataLevel() {
		return dataLevel;
		//Integer.valueOf(i)
	}


	public void setDataLevel(DataLevel dataLevel) {
		this.dataLevel = dataLevel;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public DataTypeDetail getDetail() {
		return detail;
	}


	public void setDetail(DataTypeDetail detail) {
		this.detail = detail;
	}


	public DataUsage getUsage() {
		return usage;
	}


	public void setUsage(DataUsage usage) {
		this.usage = usage;
	}
	
	
	

}
