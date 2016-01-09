package com.asksunny;

import java.util.Arrays;

import com.asksunny.codegen.data.TestDataGenerator;
import com.asksunny.codegen.file.FileGenerator;
import com.asksunny.codegen.java.JavaCodeGen;

public class DevGenerator {

	public static final String SOURCE_GEN = "scaffold";
	public static final String DATA_GEN = "data";
	public static final String FILE_GEN = "file";
	public static final String HELP = "help";
	public static final String[] APP = { SOURCE_GEN, DATA_GEN, FILE_GEN };
	public static final String[] CMDS = { HELP, SOURCE_GEN, DATA_GEN, FILE_GEN };

	public static void main(String[] args) throws Exception {
		if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase(HELP))) {
			System.out.printf("Please type in of of commands %s or help <command>", Arrays.asList(CMDS));
			return;
		} else if ((args.length == 1 && args[0].equalsIgnoreCase(SOURCE_GEN))
				|| (args.length == 2 && args[0].equalsIgnoreCase(HELP) && args[1].equalsIgnoreCase(SOURCE_GEN))) {
			JavaCodeGen.usage();
			return;
		} else if ((args.length == 1 && args[0].equalsIgnoreCase(DATA_GEN))
				|| (args.length == 2 && args[0].equalsIgnoreCase(HELP) && args[1].equalsIgnoreCase(DATA_GEN))) {
			TestDataGenerator.usage();
			return;
		} else if ((args.length == 1 && args[0].equalsIgnoreCase(FILE_GEN))
				|| (args.length == 2 && args[0].equalsIgnoreCase(HELP) && args[1].equalsIgnoreCase(FILE_GEN))) {
			FileGenerator.usage();
			return;
		} else {
			String[] params = new String[args.length - 1];
			System.arraycopy(args, 1, params, 0, args.length - 1);
			if (args[0].equalsIgnoreCase(SOURCE_GEN)) {
				JavaCodeGen.main(params);
			} else if (args[0].equalsIgnoreCase(DATA_GEN)) {
				TestDataGenerator.main(params);
			} else if (args[0].equalsIgnoreCase(FILE_GEN)) {
				FileGenerator.main(params);
			} else {
				System.out.printf("Unknow command, Please type in of of commands %s or help <command>",
						Arrays.asList(CMDS));
			}

		}
	}

}
