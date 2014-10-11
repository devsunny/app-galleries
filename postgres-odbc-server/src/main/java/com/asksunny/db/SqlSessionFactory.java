package com.asksunny.db;

import java.sql.SQLException;

public class SqlSessionFactory {

	
	
	
	public static SqlSession createSqlSession(String domain, String username, String password) throws SQLException
	{		
		SqlSession session = new LocalJDBCSqlSession();		
		session.open();
		return session;
		
	}
	
	
	private SqlSessionFactory() {
		
	}

}
