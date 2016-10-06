package com.asksunny.batch.graph.sql;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.batch.graph.BatchFlowContext;
import com.asksunny.batch.graph.FatalExecutionException;
import com.asksunny.batch.graph.WorkflowTask;

public abstract class AbstractSQLWorkflowTask extends AbstractQueryable implements WorkflowTask {
	private static final Logger logger = LoggerFactory.getLogger(AbstractSQLWorkflowTask.class);
	protected List<WorkflowTask> postSuccessTasks;
	protected List<WorkflowTask> postFailureTasks;
	protected BatchFlowContext flowContext;
	protected ExecutorService executor;

	private String taskName;

	protected abstract void executeTask() throws Exception;

	@Override
	public void run() {
		try {
			logger.info("start executing task:{}", getTaskName());
			executeTask();
			logger.info("Task execution completed:{}");
			if (getPostSuccessTasks() != null) {
				for (WorkflowTask workflowTask : getPostSuccessTasks()) {
					if (getFlowContext() != null) {
						getFlowContext().submitTask();
					}
					workflowTask.init(getFlowContext());
					workflowTask.execute(this.executor);
				}
			}
		} catch (Throwable t) {
			logger.error("Task execution error", t);
			if (getPostFailureTasks() != null) {
				for (WorkflowTask workflowTask : getPostFailureTasks()) {
					if (getFlowContext() != null) {
						getFlowContext().submitTask();
					}
					workflowTask.init(getFlowContext());
					workflowTask.execute(this.executor);
				}
			} else {
				throw new FatalExecutionException(String.format("Failed to execute task:%s", getTaskName()), t);
			}
		} finally {
			if (getFlowContext() != null) {
				getFlowContext().completeTask();
			}
		}
	}

	@Override
	public void execute(ExecutorService executor) {
		this.executor = executor;
		executor.execute(this);
	}

	@Override
	public void init(BatchFlowContext flowContext) {
		flowContext.submitTask();
		this.flowContext = flowContext;
	}

	public List<WorkflowTask> getPostSuccessTasks() {
		return postSuccessTasks;
	}

	public void setPostSuccessTasks(List<WorkflowTask> postSuccessTasks) {
		this.postSuccessTasks = postSuccessTasks;
	}

	public List<WorkflowTask> getPostFailureTasks() {
		return postFailureTasks;
	}

	public void setPostFailureTasks(List<WorkflowTask> postFailureTasks) {
		this.postFailureTasks = postFailureTasks;
	}

	public BatchFlowContext getFlowContext() {
		return flowContext;
	}

	public void setFlowContext(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}
