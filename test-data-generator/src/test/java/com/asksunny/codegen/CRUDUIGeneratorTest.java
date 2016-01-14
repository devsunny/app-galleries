package com.asksunny.codegen;

import static org.junit.Assert.*;

import org.junit.Test;

import com.asksunny.codegen.angular.CRUDUIGenerator;
import com.asksunny.codegen.java.EntityCodeGen;
import com.asksunny.codegen.java.JavaMyBatisMapperGenerator;
import com.asksunny.codegen.java.JavaRestControllerGenerator;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Schema;
import com.asksunny.schema.parser.SQLScriptLexer;
import com.asksunny.schema.parser.SQLScriptParser;

public class CRUDUIGeneratorTest {

	@Test
	public void test() throws Exception {
		CodeGenConfig config = new CodeGenConfig();
		config.setMapperPackageName("com.test.mappers");
		config.setDomainPackageName("com.test.domain");
		config.setJavaBaseDir("target/src/main/java");
		config.setMyBatisXmlBaseDir("target/src/main/java");
		config.setSpringXmlBaseDir("target/src/main/java");
		config.setRestPackageName("com.test.rest");
		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/TestAngularGen.ddl.sql"));
		SQLScriptParser tokenReader = new SQLScriptParser(lexer);
		Schema schema = tokenReader.parseSql();		
		Entity entity = schema.get("Persons1");		
		JavaMyBatisMapperGenerator gen = new JavaMyBatisMapperGenerator(config, entity);
		gen.doCodeGen();
		Entity entity2 = schema.get("Persons1");	
		JavaRestControllerGenerator gen2 = new JavaRestControllerGenerator(config, entity2);
		gen2.doCodeGen();
		
	}

}
