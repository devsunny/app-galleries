package com.asksunny.codegen;

import com.asksunny.schema.dg.CLIArguments;

public class JavaCodeGen {

	public JavaCodeGen() {
	}

	public void doCodeGen(CLIArguments cliArgs) {

	}

	public static void main(String[] args) throws Exception {
		CLIArguments cliArgs = new CLIArguments(args);
		JavaCodeGen jcg = new JavaCodeGen();
		jcg.doCodeGen(cliArgs);
	}

}
