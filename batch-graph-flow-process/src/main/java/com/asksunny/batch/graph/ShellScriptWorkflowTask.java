package com.asksunny.batch.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellScriptWorkflowTask extends AbstractWorkflowTask {
	private static final Logger logger = LoggerFactory.getLogger(ShellScriptWorkflowTask.class);
	public static final boolean WINDOW_OS = File.pathSeparatorChar == ';';
	private String shellCommand = WINDOW_OS ? "cmd.exe" : "sh";
	private String shellScriptPath = null;

	public ShellScriptWorkflowTask() {

	}

	@Override
	protected void executeTask() throws Exception {
		List<String> cmds = new ArrayList<>();
		cmds.add(shellCommand);
		if (WINDOW_OS) {
			cmds.add("/C");
		}
		cmds.add(getShellScriptPath());
		logger.info("Executing shellscript:{}", shellScriptPath);
		ProcessBuilder builder = new ProcessBuilder(cmds);
		builder.environment().putAll(System.getenv());
		builder.redirectErrorStream(true);
		Process proc = builder.start();
		InputStream in = proc.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while((line=br.readLine())!=null){
			System.out.println(line);
		}
		in.close();
		proc.waitFor();
	}

	public String getShellCommand() {
		return shellCommand;
	}

	public void setShellCommand(String shellCommand) {
		this.shellCommand = shellCommand;
	}

	public String getShellScriptPath() {
		return shellScriptPath;
	}

	public void setShellScriptPath(String shellScriptPath) {
		this.shellScriptPath = shellScriptPath;
	}

}
