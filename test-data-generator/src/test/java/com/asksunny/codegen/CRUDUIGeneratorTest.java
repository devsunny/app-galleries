package com.asksunny.codegen;

import static org.junit.Assert.*;

import org.junit.Test;

import com.asksunny.codegen.angular.CRUDUIGenerator;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Schema;
import com.asksunny.schema.parser.SQLScriptLexer;
import com.asksunny.schema.parser.SQLScriptParser;

public class CRUDUIGeneratorTest {

	@Test
	public void test() throws Exception {
		CodeGenConfig cfg = new CodeGenConfig();
		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/TestAngularGen.ddl.sql"));
		SQLScriptParser tokenReader = new SQLScriptParser(lexer);
		Schema schema = tokenReader.parseSql();		
		Entity entity = schema.get("Persons2");		
		CRUDUIGenerator gen = new CRUDUIGenerator(cfg, entity);
		System.out.println(gen.genForm());
		
	}

}
