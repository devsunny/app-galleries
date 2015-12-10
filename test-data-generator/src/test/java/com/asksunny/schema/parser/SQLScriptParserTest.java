package com.asksunny.schema.parser;

import static org.junit.Assert.*;


import java.sql.Types;

import org.junit.Test;

import com.asksunny.schema.Schema;

public class SQLScriptParserTest {

	@Test
	public void test() throws Exception{
		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/test.schema.ddl.sql"));
		SQLScriptParser tokenReader = new SQLScriptParser(lexer);
		Schema schema = tokenReader.parseSql();
		lexer.close();	
		
		System.out.println(schema);
		
		assertEquals(Types.VARCHAR, JdbcSqlTypeMap.getInstance().findJdbcType("varchar").intValue());
	}

}
