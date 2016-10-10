package com.asksunny.batch.graph.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.sql.DataSource;

public class TableCopyWorkflowTask extends AbstractSQLWorkflowTask {

	private DataSource destinationDataSource;
	private String sourceTableName;
	private String destinationTableName;
	private int batchSize = 1000;
	
	public TableCopyWorkflowTask() {
		
	}

	@Override
	protected void executeTask() throws Exception 
	{
		
		String query = String.format("SELECT * FROM %s", getSourceTableName());
		Connection conn = getDatasource().getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		StringBuffer buf = new StringBuffer();
		ResultSetMetaData rsmd = rs.getMetaData();
		
		
		

	}
	
	
	

	public DataSource getDestinationDataSource() {
		return destinationDataSource;
	}

	public void setDestinationDataSource(DataSource destinationDataSource) {
		this.destinationDataSource = destinationDataSource;
	}

	public String getSourceTableName() {
		return sourceTableName;
	}

	public void setSourceTableName(String sourceTableName) {
		this.sourceTableName = sourceTableName;
	}

	public String getDestinationTableName() {
		return destinationTableName;
	}

	public void setDestinationTableName(String destinationTableName) {
		this.destinationTableName = destinationTableName;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	
	

}
