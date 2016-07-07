package com.asksunny.tasks;

public interface ParallelTask {
	/**
	 * 
	 * @return
	 */
	public String getParallelTaskGUID();

	public void setParallelTaskGUID(String uuid);

	/**
	 * 
	 * @param args
	 * @return to indicated parameters are valid or not
	 */
	public boolean init(String[] args);

	public ParallePartitioner getParallePartitioner();

	public PartitionedTask getPartitionedTask(PartitionedTaskContext context);
	
	
}
