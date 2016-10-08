package com.asksunny.batch.graph;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

public class BatchFlowContext extends ConcurrentHashMap<String, Object> {

	private static final Logger logger = LoggerFactory.getLogger(BatchFlowContext.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractApplicationContext appContext;
	private CLIArgumentContext cliArgument = new CLIArgumentContext(new String[] {});

	private AtomicInteger submittedTask = new AtomicInteger(0);
	private AtomicInteger completedTask = new AtomicInteger(0);
	
	public BatchFlowContext() {

	}
	
	public void submitTask()
	{
		int tasks = submittedTask.incrementAndGet();
		logger.debug("Submitted Task:{}", tasks);
	}
	
	public void completeTask()
	{
		int tasks = completedTask.incrementAndGet();
		logger.debug("Completed Task:{}", tasks);
	}
	
	public int getNumberOfRunningTasks()
	{
		return submittedTask.incrementAndGet() - completedTask.incrementAndGet();
	}
	

	public BatchFlowContext(CLIArgumentContext cliArgument) {
		setCliArgument(cliArgument);
	}

	public BatchFlowContext(AbstractApplicationContext appContext, CLIArgumentContext cliArgument) {
		setAppContext(appContext);
		setCliArgument(cliArgument);
	}

	public CLIArgumentContext getCliArgument() {
		return cliArgument;
	}

	public void setCliArgument(CLIArgumentContext cliArgument) {
		this.cliArgument = cliArgument;
	}

	public AbstractApplicationContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AbstractApplicationContext appContext) {
		this.appContext = appContext;
	}

}
