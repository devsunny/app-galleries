package com.asksunny.db;

import java.io.Closeable;
import java.sql.SQLException;

public interface SqlSession extends Closeable{

	public void open() throws SQLException;
	

}
