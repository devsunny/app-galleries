package com.asksunny.tasks;

public interface PartitionedTask {

	
	public String getTaskGUID();
	public void init(String taskGuid, PartitionedTaskContext context);
	public void perform();
}
