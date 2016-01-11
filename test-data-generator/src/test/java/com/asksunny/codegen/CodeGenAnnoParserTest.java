package com.asksunny.codegen;

import java.io.StringReader;

import org.junit.*;
import static org.junit.Assert.*;

import com.asksunny.schema.parser.CodeGenAnnoLexer;
import com.asksunny.schema.parser.CodeGenAnnoParser;

public class CodeGenAnnoParserTest {

	@Test
	public void test() throws Exception{
		CodeGenAnnoLexer lexer = new CodeGenAnnoLexer(new StringReader("SEQUENCE,min=1001, max=100000.0,step=2,label='account id',varname=accountId"), 1, 13);
		CodeGenAnnoParser parser = new CodeGenAnnoParser(lexer);		
		CodeGenAnnotation anno = parser.parseCodeAnnotation();
		assertEquals(CodeGenType.SEQUENCE, anno.getCodeGenType());
		parser.close();
	}
	
	@Test
	public void test2() throws Exception{
		CodeGenAnnoLexer lexer = new CodeGenAnnoLexer(new StringReader(",min=1001, max=100000.0,step=2,label='account id',varname=accountId"), 1, 13);
		CodeGenAnnoParser parser = new CodeGenAnnoParser(lexer);		
		CodeGenAnnotation anno = parser.parseCodeAnnotation();
		assertEquals(CodeGenType.OTHER, anno.getCodeGenType());
		parser.close();
	}
	
	@Test
	public void test3() throws Exception{
		CodeGenAnnoLexer lexer = new CodeGenAnnoLexer(new StringReader("min=1001, max=100000.0,step=2,label='account id',varname=accountId"), 1, 13);
		CodeGenAnnoParser parser = new CodeGenAnnoParser(lexer);		
		CodeGenAnnotation anno = parser.parseCodeAnnotation();
		assertEquals(CodeGenType.OTHER, anno.getCodeGenType());
		parser.close();
	}
	
	
	@Test
	public void test4() throws Exception{
		CodeGenAnnoLexer lexer = new CodeGenAnnoLexer(new StringReader("min=1001, ref=abc.test, max=100000.0,step=2,label='account id',varname=accountId"), 1, 13);
		CodeGenAnnoParser parser = new CodeGenAnnoParser(lexer);		
		CodeGenAnnotation anno = parser.parseCodeAnnotation();
		assertEquals(CodeGenType.OTHER, anno.getCodeGenType());
		parser.close();
	}

}
