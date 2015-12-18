package com.asksunny.schema.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.junit.Test;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Schema;

public class EntityCodeGenTest {

	@Test
	public void testNothing() throws Exception {
		InputStream in = getClass().getResourceAsStream("/test.schema.ddl.sql");
		InputStreamReader reader = new InputStreamReader(in);
		generateDomainModel(reader);
		reader.close();
	}

	public void generateDomainModel(Reader schemaFileReader) throws Exception {
		SQLScriptParser parser = new SQLScriptParser(schemaFileReader);
		Schema schema = parser.parseSql();
		schema.buildRelationship();		
		CodeGenConfig config = new CodeGenConfig();
		//config.setJavaBaseDir("target/src/main/java");
		//config.setMyBatisXmlBaseDir("target/src/main/java");
		config.setDomainPackageName("com.asksunny.domain");
		config.setMapperPackageName("com.asksunny.mapper");
		config.setRestPackageName("com.asksunny.rest.controller");
		List<Entity> entites = schema.getAllEntities();
		for (Entity entity : entites) {
			com.asksunny.codegen.EntityCodeGen codeGen = new com.asksunny.codegen.EntityCodeGen(config, entity);
			codeGen.genCode();
			///System.out.println(codeGen.toJavaDomainObject());
			//System.out.println(codeGen.toMyBatisJavaMapper());
			//System.out.println(codeGen.toMyBatisXmlMapper());	
			//System.out.println();
			
		}
	
	}

}
