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
		Connection conn = DriverManager
				.getConnection(
						"jdbc:postgresql://localhost:5433/database?loglevel=2&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory",
						"sa", "");
		Statement stmt = conn.createStatement();
		try {
			stmt.executeUpdate("INSERT INTO test (id, name, dob, updatedtime) VALUES (2021, 'Sunny Liu1', current_date, current_timestamp)");

			ResultSet rs1 = stmt.executeQuery("SELECT * from test");
			while (rs1.next()) {
				System.out.println(rs1.getString(1));
			}
			rs1.close();

			// PreparedStatement pstmt =
			// conn.prepareStatement("SELECT * FROM TEST where id=? and name=?");
			// pstmt.setInt(1, 12);
			// pstmt.setString(2, "Test");
			// ResultSet rs1 = pstmt.executeQuery();
			// while(rs1.next()){
			// System.out.println(rs1.getString(1));
			// }
			// rs1.close();

			// PreparedStatement pstmt =
			// conn.prepareStatement("INSERT INTO TEST (id, name) values (?, ?)");
			// pstmt.setInt(1, 190);
			// pstmt.setString(2, "Test251");
			// pstmt.addBatch();
			// pstmt.setInt(1, 191);
			// pstmt.setString(2, "Test252");
			// pstmt.addBatch();
			// int[] ret = pstmt.executeBatch();
			// System.out.println("update count:" + ret[0]);
		}catch(Exception ex){
			ex.printStackTrace();
		} finally {
			stmt.close();
			conn.close();
		}
	}

}
