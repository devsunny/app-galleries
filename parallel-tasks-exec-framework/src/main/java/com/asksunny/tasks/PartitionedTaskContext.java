package com.asksunny.tasks;

import java.util.HashMap;
import java.util.UUID;

public class PartitionedTaskContext extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String parallelTaskGuid = null;
	private String className;
	private String[] cliArgs = new String[] {};
	private final String taskGuid = UUID.randomUUID().toString();
	private int partitionSequence;
	private int totalPartitions;
	

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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getPartitionSequence() {
		return partitionSequence;
	}

	public void setPartitionSequence(int partitionSequence) {
		this.partitionSequence = partitionSequence;
	}

	public int getTotalPartitions() {
		return totalPartitions;
	}

	public void setTotalPartitions(int totalPartitions) {
		this.totalPartitions = totalPartitions;
	}

	public String getParallelTaskGuid() {
		return parallelTaskGuid;
	}

}
