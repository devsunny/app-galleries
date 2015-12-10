package com.asksunny.schema.parser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Types;

import org.junit.Test;

public class SchemaDDLLexerTest {

	@Test
	public void test() throws IOException{
		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/test.schema.ddl.sql"));
		Token t = null;
		while((t=lexer.nextToken())!=null){
			System.out.println(t.getImage() + ">>>" + t.getKind());
		}
		lexer.close();
		
		
		Class<Types> typesClzz = Types.class;
		Field[] fields = typesClzz.getFields();
		for (Field field : fields) {
			System.out.println(String.format("case Types.%s:", field.getName()));
		}
		
		
	}

}
