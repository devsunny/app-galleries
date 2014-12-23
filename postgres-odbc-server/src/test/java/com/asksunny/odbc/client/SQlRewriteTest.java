package com.asksunny.odbc.client;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.asksunny.sql.SQLRewriteEngine;

public class SQlRewriteTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String sql = "INSERT INTO TEST (id, name) values ($1, $2)";
		SQLRewriteEngine.rewritePostgresParameter(sql);
		String sql2 = "SET extra_float_digits = 3";
		SQLRewriteEngine.rewritePostgresParameter(sql2);
	} 

}
