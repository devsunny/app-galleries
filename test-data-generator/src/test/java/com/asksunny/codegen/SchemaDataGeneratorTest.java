package com.asksunny.codegen;

import static org.junit.Assert.*;

import org.junit.Test;

import com.asksunny.schema.Schema;
import com.asksunny.schema.parser.SQLScriptLexer;
import com.asksunny.schema.parser.SQLScriptParser;

public class SchemaDataGeneratorTest {

	@Test
	public void test() throws Exception{
		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/test.schema.ddl.sql"));
		SQLScriptParser tokenReader = new SQLScriptParser(lexer);
		Schema schema = tokenReader.parseSql();
		schema.buildRelationship();
		
		
		
	}

}
