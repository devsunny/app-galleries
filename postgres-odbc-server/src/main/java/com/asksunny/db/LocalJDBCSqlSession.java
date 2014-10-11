package com.asksunny.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalJDBCSqlSession implements SqlSession {

	private static final String DEFAULT_ID = UUID.randomUUID().toString();
	static Logger logger = LoggerFactory.getLogger(LocalJDBCSqlSession.class);
	private String driverClassName = "org.postgresql.Driver";
	private String url = "jdbc:postgresql://localhost:5432/postgres";
	private String user = "SunnyLiu";
	private String password = "";

	private Connection connection = null;
	private HashMap<String, ExtendPreparedStatement> statements = new HashMap<String, ExtendPreparedStatement>();

	private ExtendPreparedStatement activeExtendPreparedStatement = null;
	
	
	public LocalJDBCSqlSession() throws SQLException {

	}
	
	
	
	
	public boolean getAutoCommit(boolean boolAutocommit) throws SQLException
	{
		return connection.getAutoCommit();
	}
	public void setAutoCommit(boolean boolAutocommit) throws SQLException
	{
		connection.setAutoCommit(boolAutocommit);
	}

	public void open() throws SQLException {
		try {
			Class.forName(getDriverClassName());
			if (this.user != null) {
				connection = DriverManager.getConnection(url, user, password);
			} else {
				connection = DriverManager.getConnection(url);
			}
		} catch (Exception ex) {
			throw new SQLException("Failed to connect to server", "INIT001", ex);
		}
	}

	public ExtendPreparedStatement getPreparedStatement(String name)
			throws SQLException {
		name = name==null?"":name.trim().toLowerCase();
		ExtendPreparedStatement ret = statements.get(name);
		if (ret == null){
			throw new SQLException(
					String.format("Undefined statement:[%s]", name),
					"SQLSESSON0001", 9001);
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("Matched parsed statement found;[{}]", name);
			}
		}
		return ret;
	}

	public ExtendPreparedStatement prepare(String name, String sql)
			throws SQLException {		
		// do sql rewrite here
		name = name==null?"":name.trim().toLowerCase();
		ExtendPreparedStatement pret = this.statements.remove(name);
		if(pret!=null){
			pret.close();
			//only one named statement at any time;
		}
		String rewriteSQl = sql;
		rewriteSQl = "INSERT INTO TEST (id, name) values (?, ?)";
		
		
		PreparedStatement ret = connection.prepareStatement(rewriteSQl);
		pret = new ExtendPreparedStatement(ret);
		if(logger.isDebugEnabled()) logger.debug("Statement prepared:[{}]", name);
		this.statements.put(name, pret);		
		return pret;
	}
	
	

	public ResultSet excuteQuery(String sql) throws SQLException {
		// do sql rewrite here
		String rewriteSQl = sql;
		Statement stmt = connection.createStatement();		
		ResultSet ret = stmt.executeQuery(rewriteSQl);
		if(ret!=null){
			ret = new ExtendResultSet(ret, null);  //replace null with function Maps
		}
		return ret;
	}
	
	
	public int excuteUpdate(String sql) throws SQLException {
		// do sql rewrite here
		String rewriteSQl = sql;
		Statement stmt = connection.createStatement();		
		int ret = stmt.executeUpdate(rewriteSQl);	
		stmt.close();
		return ret;
	}
	
	
	public boolean excute(String sql) throws SQLException {
		// do sql rewrite here
		String rewriteSQl = sql;
		Statement stmt = getStatement();				
		boolean ret = stmt.execute(rewriteSQl);	
		return ret;
	}
	
	public ResultSet getResultSet() throws SQLException {	
		Statement stmt = getStatement();	
		return stmt.getResultSet();
	}
	
	
	public boolean getMoreResults() throws SQLException {	
		boolean ret = getStatement().getMoreResults();	
		return ret;
	}
	
	public int getUpdateCount() throws SQLException {	
		int ret = getStatement().getUpdateCount();
		return ret;
	}
	
	private ExtendPreparedStatement getStatement() throws SQLException
	{
		ExtendPreparedStatement stmt = this.statements.get(DEFAULT_ID);
		if(stmt==null)	{	
			Statement stmts  = connection.createStatement();
			stmt = new ExtendPreparedStatement(stmts);
			this.statements.put(DEFAULT_ID, stmt);
		}
		return stmt;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public HashMap<String, ExtendPreparedStatement> getStatements() {
		return statements;
	}

	public void setStatements(
			HashMap<String, ExtendPreparedStatement> statements) {
		this.statements = statements;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;

	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;

	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;

	}
	
	
	

	public ExtendPreparedStatement getActiveExtendPreparedStatement() {
		return activeExtendPreparedStatement;
	}




	public void setActiveExtendPreparedStatement(
			ExtendPreparedStatement activeExtendPreparedStatement) {
		this.activeExtendPreparedStatement = activeExtendPreparedStatement;
	}




	@Override
	public void close() throws IOException {
		for (PreparedStatement pstmt : statements.values()) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				logger.warn("Failed to close PreparedStatement connection", e);
			}
		}

		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.warn("Failed to close sql connection", e);
			}finally{
				connection = null;
			}
		}

	}

}
