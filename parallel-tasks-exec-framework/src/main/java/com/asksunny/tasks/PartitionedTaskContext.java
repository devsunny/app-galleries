package com.asksunny.tasks;

import java.util.HashMap;
import java.util.UUID;

public class PartitionedTaskContext extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] cliArgs = new String[] {};
	private final String taskGuid = UUID.randomUUID().toString();

	public PartitionedTaskContext() {
		super();
	}

	public void init(String[] cliArgs) {
		if (cliArgs != null) {
			this.cliArgs = cliArgs;
		}
	}

	public String[] getCliArgs() {
		return cliArgs;
	}

	public String getTaskGuid() {
		return taskGuid;
	}

}
