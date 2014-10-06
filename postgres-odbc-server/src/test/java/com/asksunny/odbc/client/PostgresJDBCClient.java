package com.asksunny.odbc.client;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PostgresJDBCClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Class.forName("org.postgresql.Driver");
		Connection conn =  DriverManager.getConnection("jdbc:postgresql://localhost:5433/database?loglevel=2&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", "sa", "");
		Statement stmt = conn.createStatement();
		ResultSet rs1 = stmt.executeQuery("SELECT * from test");
		while(rs1.next()){
			System.out.println(rs1.getString(2));
		}
		rs1.close();
		stmt.close();
		
		
		conn.close();
	} 

}
