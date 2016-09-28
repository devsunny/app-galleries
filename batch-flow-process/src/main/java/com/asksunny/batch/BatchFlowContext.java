package com.asksunny.batch;

import java.util.HashMap;

import org.springframework.context.support.AbstractApplicationContext;

public class BatchFlowContext extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractApplicationContext appContext;
	private ArgumentMap cliArgument = new ArgumentMap(new String[] {});

	public BatchFlowContext() {

	}

	public BatchFlowContext(ArgumentMap cliArgument) {
		setCliArgument(cliArgument);
	}

	public BatchFlowContext(AbstractApplicationContext appContext, ArgumentMap cliArgument) {
		setAppContext(appContext);
		setCliArgument(cliArgument);
	}

	public ArgumentMap getCliArgument() {
		return cliArgument;
	}

	public void setCliArgument(ArgumentMap cliArgument) {
		this.cliArgument = cliArgument;
	}

	public AbstractApplicationContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AbstractApplicationContext appContext) {
		this.appContext = appContext;
	}

}
