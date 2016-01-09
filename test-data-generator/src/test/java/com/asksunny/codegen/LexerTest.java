package com.asksunny.codegen;

import static org.junit.Assert.*;

import org.junit.Test;

import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.Schema;
import com.asksunny.schema.parser.SQLScriptLexer;
import com.asksunny.schema.parser.SQLScriptParser;
import com.asksunny.schema.parser.Token;

public class LexerTest {

	
	@Test
	public void test2() throws Exception {
		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/unique2.sql"));
		Token t  = null;
		while((t=lexer.nextToken())!=null){
			//System.out.println(String.format("%d:%d [%s] [%s] [%s]", t.getLine(), t.getColumn(), t.getImage(), t.getKind(), t.getKeyword()));
		}		
		lexer.close();
	}
	
	

}
