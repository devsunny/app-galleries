package com.asksunny.codegen.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.asksunny.CLIArguments;
import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.codegen.CodeGenerator;
import com.asksunny.codegen.angular.AngularUIGenerator;
import com.asksunny.codegen.utils.ParamMapBuilder;
import com.asksunny.codegen.utils.TemplateUtil;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Schema;
import com.asksunny.schema.parser.SQLScriptParser;

public class JavaCodeGen extends CodeGenerator {

	public JavaCodeGen() {
		super(new CodeGenConfig(), (Schema) null);
	}

	public JavaCodeGen(CodeGenConfig configuration) {
		super(configuration, (Schema) null);
	}

	public void doCodeGen() throws IOException {
		String schemaFiles = configuration.getSchemaFiles();
		if (schemaFiles == null) {
			throw new IOException("Schema DDL file has not been specified");
		}
		Schema schema = null;
		String[] sfs = schemaFiles.split("\\s*[,;]\\s*");
		for (int i = 0; i < sfs.length; i++) {
			InputStream in = getClass().getResourceAsStream(String.format("/%s", sfs[i]));
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

		StringBuilder sqlBuffer = new StringBuilder();
		for (int i = 0; i < sfs.length; i++) {
			InputStream in = getClass().getResourceAsStream(String.format("/%s", sfs[i]));
			sqlBuffer.append(IOUtils.toString(in));
			sqlBuffer.append("\n;");
			in.close();
		}
		writeCode(new File(configuration.getSpringXmlBaseDir()), "schema_ddl.sql", sqlBuffer.toString());
		if (new File(configuration.getSpringXmlBaseDir(), "seed_data.sql").exists() == false) {
			writeCode(new File(configuration.getSpringXmlBaseDir()), "seed_data.sql", "");
		}

		if (new File(configuration.getSpringXmlBaseDir(), "log4j.xml").exists() == false) {
			String log4j = TemplateUtil.renderTemplate(getClassTemplate(getClass(), "log4j.xml.tmpl"),
					ParamMapBuilder.newBuilder().buildMap());
			writeCode(new File(configuration.getSpringXmlBaseDir()), "log4j.xml", log4j);
		}

	}

	public void doCodeGen(Schema schema) throws IOException {
		schema.buildRelationship();

		if (configuration.isGenSpringContext()) {
			SpringContextGenerator springContext = new SpringContextGenerator(configuration, schema);
			springContext.doCodeGen();
		}

		if (configuration.isGenPomXml()) {
			PomXmlGenerator pomgen = new PomXmlGenerator(configuration, schema);
			pomgen.doCodeGen();
		}

		List<Entity> entites = schema.getAllEntities();
		for (Entity entity : entites) {
			if (configuration.getIncludes().size() > 0 && !configuration.shouldInclude(entity.getName())) {
				continue;
			}
			if (configuration.shouldIgnore(entity.getName())) {
				continue;
			}

			JavaMyBatisMapperGenerator myBatisGen = new JavaMyBatisMapperGenerator(configuration, entity);
			myBatisGen.doCodeGen();

			JavaRestControllerGenerator restGen = new JavaRestControllerGenerator(configuration, entity);
			restGen.doCodeGen();

			MyBatisXmlEntityGenerator myBatisXmlGen = new MyBatisXmlEntityGenerator(configuration, entity);
			myBatisXmlGen.doCodeGen();

			JavaDomainObjectGenerator domainGen = new JavaDomainObjectGenerator(configuration, entity);
			domainGen.doCodeGen();

		}

		if (configuration.isGenAngular()) {
			AngularUIGenerator augularGenerator = new AngularUIGenerator(configuration, schema);
			augularGenerator.doCodeGen();
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
			configuration.setSchemaFiles(sfs);
		}
		String d = cliArgs.getOption("d");
		configuration.setGenDomainObject(d != null);
		if (configuration.isGenDomainObject()) {
			configuration.setDomainPackageName(d);
		}
		String m = cliArgs.getOption("m");
		configuration.setGenMyBatisMapper(m != null);
		if (configuration.isGenMyBatisMapper()) {
			configuration.setMapperPackageName(m);
		}

		String r = cliArgs.getOption("r");
		configuration.setGenRestController(r != null);
		if (configuration.isGenRestController()) {
			configuration.setRestPackageName(r);
		}

		if (!configuration.isGenDomainObject() && !configuration.isGenMyBatisMapper()
				&& !configuration.isGenRestController()) {
			valid = false;
			buf.append("Need at least one code options with argment -d, -m or -r\n");
		}

		if (!valid) {
			System.err.println(buf.toString());
			usage();
		} else {
			String ig = cliArgs.getOption("i");
			if (ig != null) {
				configuration.setIgnores(ig);
			}
			if (cliArgs.getOption("S") != null) {
				configuration.setSuffixSequenceIfExists(cliArgs.getBooleanOption("S"));
			}
			String spring = cliArgs.getOption("spring");
			if (spring != null) {
				if (spring.equalsIgnoreCase("true")) {
					configuration.setGenSpringContext(true);
				} else if (spring.equalsIgnoreCase("false")) {
					configuration.setGenSpringContext(false);
				} else {
					configuration.setGenSpringContext(true);
					configuration.setSpringXmlBaseDir(spring);
				}
			}

			if (cliArgs.getOption("j") != null) {
				configuration.setJavaBaseDir(cliArgs.getOption("j"));
			}

			if (cliArgs.getOption("x") != null) {
				configuration.setMyBatisXmlBaseDir(cliArgs.getOption("x"));
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

}
