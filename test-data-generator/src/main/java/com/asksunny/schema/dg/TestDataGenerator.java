package com.asksunny.schema.dg;

import java.io.File;

import com.asksunny.CLIArguments;
import com.asksunny.schema.SchemaDataGenerator;
import com.asksunny.schema.SchemaOutputType;
import com.asksunny.schema.parser.SQLScriptParser;

public class TestDataGenerator {

	public static void usage() {
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
		SchemaDataGenerator dg = new SchemaDataGenerator();
		try {
			dg.setNumberOfRecords(numStr);
			dg.setOutputType(SchemaOutputType.valueOf(type.toUpperCase()));
			dg.setOutputUri(outfile);
			dg.setSchema(parser.parseSql());
			dg.generateData();
		} finally {
			dg.close();
			parser.close();
		}
	}

}
