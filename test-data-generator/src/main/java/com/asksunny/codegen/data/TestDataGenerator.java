package com.asksunny.codegen.data;

import java.io.File;

import com.asksunny.CLIArguments;
import com.asksunny.schema.parser.SQLScriptParser;

public class TestDataGenerator {

	public static void usage() {
		System.err.println("Desc : TestDataGenerator is a tool to generate sample according to raltional");
		System.err.println("       database schema DDL file as input; it can generate sample data with relationship,");
		System.err.println("       the output type can be sql insert statement, CSV file and planned JDBC target. \n");
		System.err.println("       schema file can be annotated to geneated more specific data. \n");
		System.err.println("Usage: TestDataGenerator <option>...");
		System.err.println("         Required:");
		System.err.println("                    -s <path_to_ddl>");
		System.err.println("                    -o <path_to_output_directory>");
		System.err.println("         Optional:");
		System.err.println("                    -n <number_records> default 1000");
		System.err.println("                    -t <type[INSERT|CSV|JDBC> default csv");
		System.err.println("	                -driverClass <jdbc_driver_class>");
		System.err.println("	                -url <jdbc_url>");
		System.err.println("	                -user <jdbc_user>");
		System.err.println("	                -password <jdbc_password>");
		System.err.println("examples:");
		System.err.println("         TestDataGenerator -s myschema.ddl.sql -t INSERT -n 100000");
	}

	public static void main(String[] args) throws Exception {
		CLIArguments cli = new CLIArguments(args);
		String file = cli.getOption("s");
		String outfile = cli.getOption("o");
		long numStr = cli.getLongOption("n", 1000);
		String type = cli.getOption("t", "CSV");
		if (file == null || outfile == null) {
			usage();
			return;
		}

		File f = new File(file);
		if (!f.exists()) {
			System.err.printf("Script file %s does not exists\n", file);
			return;
		}
		File of = new File(outfile);
		if (!of.exists()) {
			System.err.printf("Outout Directory %s does not exists\n", outfile);
			return;
		}
		SQLScriptParser parser = new SQLScriptParser(f);
		BottomUpSchemaDataGenerator dg = null;
		try {
			SchemaDataConfig config = new SchemaDataConfig();
			config.setNumberOfRecords(numStr);
			config.setOutputType(SchemaOutputType.valueOf(type.toUpperCase()));
			config.setOutputUri(outfile);
			dg = new BottomUpSchemaDataGenerator(parser.parseSql());
			dg.setConfig(config);
			dg.generateData();
		} finally {
			if (dg != null) {
				dg.close();
			}
			parser.close();
		}
	}

}
