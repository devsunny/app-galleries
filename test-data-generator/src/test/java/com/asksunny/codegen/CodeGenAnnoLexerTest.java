package com.asksunny.codegen;

import java.io.StringReader;

import org.junit.Test;

import com.asksunny.io.LHTokenReader;
import com.asksunny.io.LexerToken;
import com.asksunny.schema.parser.CodeGenAnnoLexer;
import com.asksunny.schema.parser.CodeGenAnnoToken;

public class CodeGenAnnoLexerTest {

	@Test
	public void test() throws Exception {
		CodeGenAnnoLexer lexer = new CodeGenAnnoLexer(new StringReader("SEQUENCE,min=1001, max=100000.0,step=2,uiname='account id',varname=accountId"), 1, 13);
		LHTokenReader lexer2 = new LHTokenReader(3, lexer);
		LexerToken token = null;
		while((token=lexer2.nextToken())!=null){
			System.out.println(token);
		}		
	}

}
