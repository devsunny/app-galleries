package com.asksunny.tasks;

import java.util.HashMap;

public class ParallelTaskContext extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] cliArgs = new String[]{};

	public ParallelTaskContext() {
		super();
	}
	
	public void init(String[] cliArgs)
	{
		if(cliArgs!=null){
			this.cliArgs = cliArgs;
		}
	}

	public String[] getCliArgs() {
		return cliArgs;
	}
	
	
}
