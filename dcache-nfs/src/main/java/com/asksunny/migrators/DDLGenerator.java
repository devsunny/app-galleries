package com.asksunny.migrators;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DDLGenerator {
	private static Logger logger = LoggerFactory.getLogger(DDLGenerator.class);
	
	private DataSource datasource;
	
	public DDLGenerator() {
		
	}
	
	public void init()
	{
		
		 Connection connection = null;
	        String name = "unknown";
	        try {
	            connection = getDataSource().getConnection();
	            Database database =
	                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
	           
	            logger.warn(database.getClass().getName());
	            
	            
	            name = database.getDatabaseProductName();
	        }  catch (DatabaseException e) {
	        	logger.warn("problem closing connection", e);
	        }catch (SQLException e) {
	        	logger.warn("problem closing connection", e);
	        } finally {
	            if (connection != null) {
	                try {
	                    if (!connection.getAutoCommit()) {
	                        connection.rollback();
	                    }
	                    connection.close();
	                } catch (Exception e) {
	                	logger.warn("problem closing connection", e);
	                }
	            }
	        }
		
	}
	
	

	public DataSource getDataSource() {
		return datasource;
	}

	public void setDataSource(DataSource datasource) {
		this.datasource = datasource;
	}
	
	
	

}
