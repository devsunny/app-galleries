package com.asksunny.tasks;

public interface ParallelTask {
	/**
	 * 
	 * @return
	 */
	public String getParallelTaskGUID();

	public void setParallelTaskGUID(String uuid);

	public void init(ParallelTaskContext context);

	public ParallePartitioner getParallePartitioner();

	public PartitionedTask getPartitionedTask(PartitionedTaskContext context);
	
	
}
