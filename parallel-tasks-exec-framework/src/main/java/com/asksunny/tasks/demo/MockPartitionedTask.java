package com.asksunny.tasks.demo;

import com.asksunny.tasks.PartitionedTask;
import com.asksunny.tasks.PartitionedTaskContext;

public class MockPartitionedTask implements PartitionedTask {

	public MockPartitionedTask() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTaskGUID() {
		
		return null;
	}

	@Override
	public void init(String taskGuid, PartitionedTaskContext context) {
		

	}

	@Override
	public void perform() {
		// TODO Auto-generated method stub

	}

}
