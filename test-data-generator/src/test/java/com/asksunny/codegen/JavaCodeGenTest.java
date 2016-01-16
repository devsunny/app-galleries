package com.asksunny.codegen;

import static org.junit.Assert.*;

import org.junit.Test;

import com.asksunny.codegen.CodeGenConfig.CodeOverwriteStrategy;
import com.asksunny.codegen.java.JavaCodeGen;
import com.asksunny.codegen.java.JavaMyBatisMapperGenerator;
import com.asksunny.codegen.java.JavaRestControllerGenerator;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Schema;
import com.asksunny.schema.parser.SQLScriptLexer;
import com.asksunny.schema.parser.SQLScriptParser;

public class JavaCodeGenTest {

	@Test
	public void test() throws Exception {
		CodeGenConfig config = new CodeGenConfig();
		config.setMapperPackageName("com.xperia.management.mappers");
		config.setDomainPackageName("com.xperia.management.domain");
		config.setRestPackageName("com.xperia.management.web.rest");
		config.setGenAngularController(true);
		config.setGenAngularRoute(true);
		config.setGenAngularView(true);
		config.setGenMyBatisMapper(true);
		config.setGenMyBatisSpringBeans(true);
		config.setGenSpringContext(true);		
		config.setJavaBaseDir("target/src/main/java");
		config.setMyBatisXmlBaseDir("target/src/main/resources");
		config.setSpringXmlBaseDir("target/src/main/resources");
		config.setWebBaseSrcDir("target/src/main/resources/META-INF/app");
		config.setWebappContext("management");	
		config.setAppBootstrapClassName("ManConsoleBoostrap");		
		config.setOverwriteStrategy(CodeOverwriteStrategy.OVERWRITE);		
		config.setGenPomXml(false);
		
		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/TestAngularGen.ddl.sql"));
		SQLScriptParser tokenReader = new SQLScriptParser(lexer);
		Schema schema = tokenReader.parseSql();		
		JavaCodeGen javaGen  = new JavaCodeGen(config);
		javaGen.doCodeGen(schema);
	}

}
