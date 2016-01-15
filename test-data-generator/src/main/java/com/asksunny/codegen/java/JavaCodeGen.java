package com.asksunny.codegen.java;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.asksunny.CLIArguments;
import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Schema;
import com.asksunny.schema.parser.SQLScriptParser;

public class JavaCodeGen {

	private CodeGenConfig configureation = new CodeGenConfig();

	public JavaCodeGen() {
	}

	public JavaCodeGen(CodeGenConfig configureation) {
		super();
		this.configureation = configureation;
	}

	public void doCodeGen() throws Exception {
		String schemaFiles = configureation.getSchemaFiles();
		if (schemaFiles == null) {
			throw new IOException("Schema DDL file has not been specified");
		}
		Schema schema = null;
		String[] sfs = schemaFiles.split("\\s*[,;]\\s*");
		for (int i = 0; i < sfs.length; i++) {
			InputStream in = getClass().getResourceAsStream(sfs[i]);
			if (in == null) {
				in = new FileInputStream(sfs[i]);
			}
			try {
				SQLScriptParser parser = new SQLScriptParser(new InputStreamReader(in));
				Schema schemax = parser.parseSql();
				if (schema == null) {
					schema = schemax;
				} else {
					schema.getAllEntities().addAll(schemax.getAllEntities());
				}
			} finally {
				in.close();
			}
		}
		doCodeGen(schema);

	}

	public void doCodeGen(Schema schema) throws Exception {
		schema.buildRelationship();
		
		if(configureation.isGenSpringContext()){
			SpringContextGenerator springContext = new SpringContextGenerator(configureation, schema);
			springContext.doCodeGen();
		}
		
		List<Entity> entites = schema.getAllEntities();
		for (Entity entity : entites) {
			if (configureation.getIncludes().size() > 0 && !configureation.shouldInclude(entity.getName())) {
				continue;
			}
			if (configureation.shouldIgnore(entity.getName())) {
				continue;
			}
			
			JavaMyBatisMapperGenerator myBatisGen = new JavaMyBatisMapperGenerator(configureation, entity);
			myBatisGen.doCodeGen();
			
			JavaRestControllerGenerator restGen = new JavaRestControllerGenerator(configureation, entity);
			restGen.doCodeGen();
			
			MyBatisXmlEntityGenerator myBatisXmlGen = new MyBatisXmlEntityGenerator(configureation, entity);
			myBatisXmlGen.doCodeGen();
			
		}
		
		

	}

	public boolean validateArguments(CLIArguments cliArgs) {
		boolean valid = true;
		StringBuilder buf = new StringBuilder();
		String sfs = cliArgs.getOption("s");
		boolean good = sfs != null;
		if (!good) {
			valid = good;
			buf.append("Missing schema files with argment -s\n");
		} else {
			configureation.setSchemaFiles(sfs);
		}
		String d = cliArgs.getOption("d");
		configureation.setGenDomainObject(d != null);
		if (configureation.isGenDomainObject()) {
			configureation.setDomainPackageName(d);
		}
		String m = cliArgs.getOption("m");
		configureation.setGenMyBatisMapper(m != null);
		if (configureation.isGenMyBatisMapper()) {
			configureation.setMapperPackageName(m);
		}

		String r = cliArgs.getOption("r");
		configureation.setGenRestController(r != null);
		if (configureation.isGenRestController()) {
			configureation.setRestPackageName(r);
		}

		if (!configureation.isGenDomainObject() && !configureation.isGenMyBatisMapper()
				&& !configureation.isGenRestController()) {
			valid = false;
			buf.append("Need at least one code options with argment -d, -m or -r\n");
		}

		if (!valid) {
			System.err.println(buf.toString());
			usage();
		} else {
			String ig = cliArgs.getOption("i");
			if (ig != null) {
				configureation.setIgnores(ig);
			}
			if (cliArgs.getOption("S") != null) {
				configureation.setSuffixSequenceIfExists(cliArgs.getBooleanOption("S"));
			}
			String spring = cliArgs.getOption("spring");
			if (spring != null) {
				if (spring.equalsIgnoreCase("true")) {
					configureation.setGenSpringContext(true);
				} else if (spring.equalsIgnoreCase("false")) {
					configureation.setGenSpringContext(false);
				} else {
					configureation.setGenSpringContext(true);
					configureation.setSpringXmlBaseDir(spring);
				}
			}

			if (cliArgs.getOption("j") != null) {
				configureation.setJavaBaseDir(cliArgs.getOption("j"));
			}

			if (cliArgs.getOption("x") != null) {
				configureation.setMyBatisXmlBaseDir(cliArgs.getOption("x"));
			}
		}

		return valid;
	}

	public static void usage() {
		System.err.println("Desc : JavaCodeGen is a tool to generate scaffold of CRUD type Restful service.");
		System.err.println("       It takes raltional database schema DDL file as input; it can generate ");
		System.err.println("       domain object java source file, mybatis Mapper java source and xml  ");
		System.err.println("       mapping files, spring restful service based rest controll, maven and");
		System.err.println("       maven pom file, jetty booststrap spring context xml and restful application");
		System.err.println("       spring context\n");
		System.err.println("Usage: JavaCodeGen <options>...");
		System.err.println("       Required:");
		System.err.println("                   -s  <schema_files> - comma separted file paths");
		System.err.println("       Optional:");
		System.err.println(
				"                   -d  <domain_pkg_name> - domain object package names, ie 'com.asksunny.domain'");
		System.err.println(
				"                   -m  <mapper_pkg_name> - myBatis mapper package names, ie 'com.asksunny.mapper'");
		System.err.println(
				"                   -r  <rest_pkg_name> - rest controller package names, ie 'com.asksunny.rest.controller'");
		System.err
				.println("                   -i  <ignore_tbnames> - comma separated list of table names to be ignored");
		System.err.println(
				"                   -S                    - generated new file with sequence number suffix if already exist.");

		System.err.println("                   -j  <java_source_dir> - default 'src/main/java'");

		System.err.println("                   -x  <mybatis_xml_dir> - default 'src/main/resources'");
		System.err.println("                   -spring  <spring_xml_dir|true|false> - default 'src/main/resources'");
	}

	public static void main(String[] args) throws Exception {
		CLIArguments cliArgs = new CLIArguments(args);
		JavaCodeGen jcg = new JavaCodeGen();
		if (jcg.validateArguments(cliArgs)) {
			jcg.doCodeGen();
		}
	}

	public CodeGenConfig getConfigureation() {
		return configureation;
	}

	public void setConfigureation(CodeGenConfig configureation) {
		this.configureation = configureation;
	}

}
