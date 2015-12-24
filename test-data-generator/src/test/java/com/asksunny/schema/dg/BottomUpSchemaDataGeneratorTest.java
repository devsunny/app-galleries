package com.asksunny.schema.dg;

import static org.junit.Assert.*;

import org.junit.Test;

import com.asksunny.schema.BottomUpSchemaDataGenerator;
import com.asksunny.schema.DataGenType;
import com.asksunny.schema.Schema;
import com.asksunny.schema.SchemaDataConfig;
import com.asksunny.schema.SchemaOutputType;
import com.asksunny.schema.parser.SQLScriptLexer;
import com.asksunny.schema.parser.SQLScriptParser;

public class BottomUpSchemaDataGeneratorTest {

	@Test
	public void test() throws Exception {
		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/test.schema.ddl.sql"));
		SQLScriptParser tokenReader = new SQLScriptParser(lexer);
		Schema schema = tokenReader.parseSql();		
		BottomUpSchemaDataGenerator sgen = new BottomUpSchemaDataGenerator(schema);
		SchemaDataConfig config = new SchemaDataConfig();
		config.setNumberOfRecords(100);
		config.setOutputType(SchemaOutputType.INSERT);
		config.setOutputUri(null);
		//config.setDebug(true);
		sgen.setConfig(config);
		sgen.generateData();
	}

}
