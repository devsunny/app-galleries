package com.asksunny.batch.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoOpWorkflowTask extends AbstractWorkflowTask {

	private static final Logger logger = LoggerFactory.getLogger(NoOpWorkflowTask.class);
	
	public NoOpWorkflowTask() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void executeTask() throws Exception {		
		logger.info("Nothing has been executed");
	}

}
