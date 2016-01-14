package com.asksunny.codegen;

import org.junit.Test;

import com.asksunny.codegen.angular.AngularFieldGenerator;
import com.asksunny.codegen.java.MyBatisXmlEntityGenerator;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.Schema;
import com.asksunny.schema.parser.SQLScriptLexer;
import com.asksunny.schema.parser.SQLScriptParser;

import static org.junit.Assert.*;

public class MyBatisGeneratorTest {

	@Test
	public void test() throws Exception {
		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/TestAngularGen.ddl.sql"));
		SQLScriptParser tokenReader = new SQLScriptParser(lexer);
		Schema schema = tokenReader.parseSql();
		Entity entity = schema.get("Persons2");
		CodeGenConfig config = new CodeGenConfig();
		config.setMapperPackageName("com.test");
		config.setDomainPackageName("com.test");
		config.setJavaBaseDir("target/src/main/java");
		config.setMyBatisXmlBaseDir("target/src/main/java");
		config.setSpringXmlBaseDir("target/src/main/java");
		
		MyBatisXmlEntityGenerator codegen = new MyBatisXmlEntityGenerator(config, entity);
		System.out.println(codegen.genSelectByGroup());

	}

}
