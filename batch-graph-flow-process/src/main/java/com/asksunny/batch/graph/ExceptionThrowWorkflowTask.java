package com.asksunny.batch.graph;

public class ExceptionThrowWorkflowTask extends AbstractWorkflowTask {

	public ExceptionThrowWorkflowTask() {		
	}

	@Override
	protected void executeTask() throws Exception {
		throw new Exception("Are you really expecting me?");
	}

}
