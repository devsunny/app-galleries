package com.asksunny.tasks.demo;

import com.asksunny.tasks.ParallePartitioner;
import com.asksunny.tasks.ParallelTask;
import com.asksunny.tasks.ParallelTaskContext;
import com.asksunny.tasks.PartitionedTask;
import com.asksunny.tasks.PartitionedTaskContext;

public class MockParallelTask implements ParallelTask {

	public MockParallelTask() {		
	}

	@Override
	public String getParallelTaskGUID() {
		
		return null;
	}

	@Override
	public void setParallelTaskGUID(String uuid) {	
	}

	@Override
	public void init(ParallelTaskContext context) 
	{
		
	}

	@Override
	public ParallePartitioner getParallePartitioner() {		
		return new MockTaskPartitioner();
	}

	@Override
	public PartitionedTask getPartitionedTask(PartitionedTaskContext context) {
		
		PartitionedTask pTask = new MockPartitionedTask();
		pTask.init(context.getTaskGuid(), context);
		return pTask;
	}

}
