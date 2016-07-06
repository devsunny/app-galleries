package com.asksunny.tasks.demo;

import java.util.List;

import com.asksunny.tasks.ParallePartitioner;
import com.asksunny.tasks.ParallelTaskContext;
import com.asksunny.tasks.PartitionedTaskContext;

public class MockTaskPartitioner implements ParallePartitioner{

	public MockTaskPartitioner() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(ParallelTaskContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<PartitionedTaskContext> doPartition() {
		// TODO Auto-generated method stub
		return null;
	}

}
