package com.asksunny.tasks;

import java.util.List;

public interface ParallePartitioner {

	public void init(String[] args);

	/**
	 * If negative or 0 return, TaskMaster will wait forever; otherwise it will
	 * wait until the specified time. TaskAgent may chose to kill the
	 * partitionedtask
	 * 
	 * @return
	 */
	public long getTimeout();

	public List<String[]> doPartition();
}
