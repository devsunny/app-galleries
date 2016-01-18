package codegen;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.codegen.CodeGenConfig.CodeOverwriteStrategy;
import com.asksunny.codegen.data.BottomUpSchemaDataGenerator;
import com.asksunny.codegen.data.SchemaDataConfig;
import com.asksunny.codegen.data.SchemaOutputType;
import com.asksunny.codegen.java.JavaCodeGen;
import com.asksunny.schema.Schema;
import com.asksunny.schema.parser.SQLScriptLexer;
import com.asksunny.schema.parser.SQLScriptParser;

public class CodeGenTool {

	@Test
	@Ignore
	public void genApp() throws Exception {
		CodeGenConfig config = new CodeGenConfig();
		config.setBaseSrcDir(".");
		config.setBasePackageName("com.xperia.management");
		config.setWebappContext("management");		
		config.setAppBootstrapClassName("ManConsoleBoostrap");
		config.setOverwriteStrategy(CodeOverwriteStrategy.OVERWRITE);
		config.setSchemaFiles("TestAngularGen.ddl.sql");
		JavaCodeGen javaGen = new JavaCodeGen(config);
		javaGen.doCodeGen();
	}
	
	@Test
	
	public void genData() throws Exception {
		SQLScriptLexer lexer = new SQLScriptLexer(getClass().getResourceAsStream("/TestAngularGen.ddl.sql"));
		SQLScriptParser tokenReader = new SQLScriptParser(lexer);
		Schema schema = tokenReader.parseSql();		
		BottomUpSchemaDataGenerator sgen = new BottomUpSchemaDataGenerator(schema);
		SchemaDataConfig config = new SchemaDataConfig();
		config.setNumberOfRecords(500);
		config.setOutputType(SchemaOutputType.INSERT);
		config.setOutputUri("src/test/resources");
		//config.setDebug(true);
		sgen.setConfig(config);
		sgen.generateData();
	}

}
