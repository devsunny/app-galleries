package com.asksunny.schema.parser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class SchemaDDLLexerTest {

	@Test
	public void test() throws IOException{
		SchemaDDLLexer lexer = new SchemaDDLLexer(getClass().getResourceAsStream("/test.schema.ddl.sql"));
		Token t = null;
		while((t=lexer.nextToken())!=null){
			System.out.println(t.getImage() + ">>>" + t.getKind());
		}
	}

}
