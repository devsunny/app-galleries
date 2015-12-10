package com.asksunny.schema.parser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SQLScriptLookaheadTokenReaderTest {

	@Test
	public void test() throws IOException {
		List<Token> tokens1 = new ArrayList<>();
		List<Token> tokens2 = new ArrayList<>();

		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/test.schema.ddl.sql"));
		Token t = null;
		while ((t = lexer.nextToken()) != null) {
			tokens1.add(t);
		}
		lexer.close();

		lexer = new SQLScriptLexer(getClass().getResourceAsStream("/test.schema.ddl.sql"));
		SQLScriptLookaheadTokenReader tokenReader = new SQLScriptLookaheadTokenReader(3, lexer);
		while ((t = tokenReader.read()) != null) {
			tokens2.add(t);
		}
		lexer.close();
		
		assertEquals(tokens1.size(), tokens2.size());
		assertEquals(tokens1.get(10).toString(), tokens2.get(10).toString());
	}

}
