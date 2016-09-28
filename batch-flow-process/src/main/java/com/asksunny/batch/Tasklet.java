package com.asksunny.batch;

public interface Tasklet {
	void init(BatchFlowContext context);

	String[] execute();

	public String[] getFailedTaskIds();

	public String[] getSuccessTaskIds();
}
