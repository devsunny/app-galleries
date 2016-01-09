package com.asksunny.codegen;

import static org.junit.Assert.*;

import org.junit.Test;

import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.Schema;
import com.asksunny.schema.parser.SQLScriptLexer;
import com.asksunny.schema.parser.SQLScriptParser;

public class UniqueConstraintTest2 {

	@Test
	public void test() throws Exception {
		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/unique3.sql"));
		SQLScriptParser tokenReader = new SQLScriptParser(lexer);
		tokenReader.setDebug(true);
		Schema schema = tokenReader.parseSql();		
		Entity entity = schema.get("Persons2");
		assertNotNull(entity);
		Field fd = entity.findField("P_Id");
		assertTrue(fd.isUnique());		
		
		
		Entity entity3 = schema.get("Persons3");
		assertNotNull(entity3);
		Field fd3 = entity3.findField("P_Id");
		assertTrue(fd3.isUnique());		
		assertTrue(fd3.isPrimaryKey());		
		
		Entity entity4 = schema.get("Persons4");
		assertNotNull(entity4);
		Field fd4 = entity4.findField("P_Id");
		assertTrue(fd4.isUnique());		
		assertTrue(fd4.isPrimaryKey());	
		
	}	
	
}
