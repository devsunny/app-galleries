package com.asksunny.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BatchFlowBootstrap {
	private static Logger logger = LoggerFactory.getLogger(BatchFlowBootstrap.class);

	public BatchFlowBootstrap() {
	}

	public static void main(String[] args) {
		ArgumentMap argsmap = new ArgumentMap(args);
		AbstractApplicationContext appContext = null;
		try {
			appContext = new ClassPathXmlApplicationContext(new String[] { "datasource-context.xml" });
			BatchFlowContext flowContext = new BatchFlowContext(appContext, argsmap);
			TaskletExecutor executor = new TaskletExecutor(flowContext, args[0]);
			executor.run();
			if (executor.getErrorType() == ErrorType.FATAL) {
				logger.error("Failed to execute task:{}", "tasklet");
			}
		} catch (BeansException e) {
			if (appContext != null) {
				appContext.close();
			}
			e.printStackTrace();
			System.exit(1);
		}
	}

}
