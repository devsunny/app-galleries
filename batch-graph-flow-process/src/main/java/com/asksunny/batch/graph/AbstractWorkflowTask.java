package com.asksunny.batch.graph;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWorkflowTask implements WorkflowTask {

	private static final Logger logger = LoggerFactory.getLogger(AbstractWorkflowTask.class);
	protected List<WorkflowTask> postSuccessTasks;
	protected List<WorkflowTask> postFailureTasks;
	protected BatchFlowContext flowContext;
	protected ExecutorService executor;
	protected FlowTaskParameterType flowTaskParameterType = FlowTaskParameterType.None;
	protected String taskParameterName = null;

	private String taskName;

	public AbstractWorkflowTask() {
	}

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

	public Object getTaskParameter() {
		switch (flowTaskParameterType) {
		case CLIArgumentContext:
			return getFlowContext().getCliArgument();
		case BatchFlowContext:
			return getFlowContext();
		case CLIArgument:
			return getFlowContext().getCliArgument().get(getTaskParameterName());
		case BatchFlowContextObject:
			return getFlowContext().get(getTaskParameterName());
		case None:
			return null;
		default:
			return null;
		}
	}

	@Override
	public void execute(ExecutorService executor) {
		this.executor = executor;
		executor.execute(this);
	}

	@Override
	public String getTaskName() {
		return this.taskName;
	}

	@Override
	public void init(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
	}

	@Override
	public List<WorkflowTask> getPostSuccessTasks() {
		return postSuccessTasks;
	}

	@Override
	public List<WorkflowTask> getPostFailureTasks() {
		return postFailureTasks;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public BatchFlowContext getFlowContext() {
		return flowContext;
	}

	public void setFlowContext(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setPostSuccessTasks(List<WorkflowTask> postSuccessTasks) {
		this.postSuccessTasks = postSuccessTasks;
	}

	public void setPostFailureTasks(List<WorkflowTask> postFailureTasks) {
		this.postFailureTasks = postFailureTasks;
	}

	public FlowTaskParameterType getFlowTaskParameterType() {
		return flowTaskParameterType;
	}

	public void setFlowTaskParameterType(FlowTaskParameterType flowTaskParameterType) {
		this.flowTaskParameterType = flowTaskParameterType;
	}

	public String getTaskParameterName() {
		return taskParameterName;
	}

	public void setTaskParameterName(String taskParameterName) {
		this.taskParameterName = taskParameterName;
	}

}
