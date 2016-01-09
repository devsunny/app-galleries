package com.asksunny.codegen.data;

import java.io.Serializable;

public class SchemaDataConfig implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SchemaOutputType outputType;
	private String outputUri;	
	private long numberOfRecords;
	private boolean debug = false;
	
	public SchemaOutputType getOutputType() {
		return outputType;
	}
	public void setOutputType(SchemaOutputType outputType) {
		this.outputType = outputType;
	}
	public String getOutputUri() {
		return outputUri;
	}
	public void setOutputUri(String outputUri) {
		this.outputUri = outputUri;
	}
	public long getNumberOfRecords() {
		return numberOfRecords;
	}
	public void setNumberOfRecords(long numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	

}
