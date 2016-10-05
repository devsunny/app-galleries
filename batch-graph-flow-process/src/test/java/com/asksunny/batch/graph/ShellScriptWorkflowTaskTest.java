package com.asksunny.batch.graph;

import static org.junit.Assert.*;

import org.junit.Test;

public class ShellScriptWorkflowTaskTest {

	@Test
	public void test() {
		ShellScriptWorkflowTask task = new ShellScriptWorkflowTask();
		task.setTaskName("TEST");
		if (ShellScriptWorkflowTask.WINDOW_OS) {
			task.setShellScriptPath(
					getClass().getResource("/test-script.bat").getFile().substring(1).replace('/', '\\'));
		} else {
			task.setShellScriptPath(getClass().getResource("/test-script.bat").getFile());
		}
		task.run();
	}

}
