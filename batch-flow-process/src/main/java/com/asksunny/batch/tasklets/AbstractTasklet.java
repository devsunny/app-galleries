package com.asksunny.batch.tasklets;

import com.asksunny.batch.BatchFlowContext;
import com.asksunny.batch.Tasklet;

public class AbstractTasklet implements Tasklet {

	protected BatchFlowContext flowContext;
	protected String[] failedTaskIds;
	protected String[] successTaskIds;

	public AbstractTasklet() {

	}

	@Override
	public void init(BatchFlowContext context) {
		setFlowContext(context);
	}

	@Override
	public String[] execute() {
		return null;
	}

	public BatchFlowContext getFlowContext() {
		return flowContext;
	}

	public void setFlowContext(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
	}

	public String[] getFailedTaskIds() {
		return failedTaskIds;
	}

	public void setFailedTaskIds(String[] failedTaskIds) {
		this.failedTaskIds = failedTaskIds;
	}

	public String[] getSuccessTaskIds() {
		return successTaskIds;
	}

	public void setSuccessTaskIds(String[] successTaskIds) {
		this.successTaskIds = successTaskIds;
	}

}
