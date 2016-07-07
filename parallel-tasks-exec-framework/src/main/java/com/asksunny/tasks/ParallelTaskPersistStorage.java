package com.asksunny.tasks;

public interface ParallelTaskPersistStorage 
{
	
	void addToQueue(PartitionedTaskContext taskContext);
	void updatePartitionTaskStatus(String guid, PartitionedTaskStatus status);
	TaskStatus getParallelTaskStatus(String parallelTaskGuid);
	PartitionedTaskContext getNextWaitingTask();
	
}
