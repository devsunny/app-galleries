package org.postgresql.test;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.asksunny.odbc.SQLScriptReader;

public class SqlScriptReaderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		String sqlscript = "select * from test;insert into test (id, name) values (123, 'name')";
		SQLScriptReader reader = new SQLScriptReader(new StringReader(sqlscript));
		String sql = null;
		while((sql=reader.readStatement())!=null)
		{
			System.out.println(sql);
		}
	}

}
