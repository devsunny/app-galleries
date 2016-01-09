package com.asksunny.codegen;

import static org.junit.Assert.*;

import org.junit.Test;

import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.Schema;
import com.asksunny.schema.parser.SQLScriptLexer;
import com.asksunny.schema.parser.SQLScriptParser;

public class UniqueConstraintTest {

	@Test
	public void test() throws Exception {
		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/unique.index.sql"));
		SQLScriptParser tokenReader = new SQLScriptParser(lexer);
		tokenReader.setDebug(false);
		Schema schema = tokenReader.parseSql();		
		Entity entity = schema.get("Persons");
		assertNotNull(entity);
		Field fd = entity.findField("P_Id");
		assertTrue(fd.isUnique());		
	}
	
	@Test
	public void test2() throws Exception {
		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/unique2.sql"));
		SQLScriptParser tokenReader = new SQLScriptParser(lexer);
		tokenReader.setDebug(true);
		Schema schema = tokenReader.parseSql();		
		Entity entity = schema.get("Persons2");
		assertNotNull(entity);
		Field fd = entity.findField("P_Id");
		assertTrue(fd.isUnique());	
		
		Entity entity3 = schema.get("Persons3");
		assertNotNull(entity3);
		Field fd2 = entity3.findField("P_Id");
		assertTrue(fd2.isPrimaryKey());	
		
	}
	
	

}
