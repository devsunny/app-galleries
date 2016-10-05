package com.asksunny.batch.graph;

import java.util.List;
import java.util.concurrent.ExecutorService;

public interface WorkflowTask extends Runnable {

	String getTaskName();

	void init(BatchFlowContext flowContext);

	void execute(ExecutorService executor);

	List<WorkflowTask> getPostSuccessTasks();
	
	List<WorkflowTask> getPostFailureTasks();
}
