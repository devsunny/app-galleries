package com.asksunny.batch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BatchFlowBootstrap {
	private static Logger logger = LoggerFactory.getLogger(BatchFlowBootstrap.class);

	public BatchFlowBootstrap() {
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage:");
			System.err.println(
					"   java BatchFlowBootstrap <spring_context_xml> <taskletId> [<additional_name_value_pair_parameter>...]");
			System.err.println("Examples:");
			System.err
					.println(" java BatchFlowBootstrap myAppContext.xml taskletBeanId1,taskletBeanId2,taskletBeanId3");
			System.err.println(" java BatchFlowBootstrap myAppContext.xml taskletBeanId1 -tdate 20160909 -maxcount 20");
			System.exit(1);
		}

		ArgumentMap argsmap = new ArgumentMap(args);
		AbstractApplicationContext appContext = null;
		try {
			appContext = new ClassPathXmlApplicationContext(args[0].split(","));
			BatchFlowContext flowContext = new BatchFlowContext(appContext, argsmap);
			String[] tasks = args[1].split(",");
			if (tasks.length == 1) {
				TaskletExecutor executor = new TaskletExecutor(flowContext, tasks[0]);
				executor.run();
				if (executor.getErrorType() == ErrorType.FATAL) {
					logger.error("Encounter unrecoverable error while executing task:{}", tasks[0]);
					System.exit(1);
				}
			} else {
				ExecutorService executors = Executors.newFixedThreadPool(tasks.length);
				TaskletExecutor[] tasklets = new TaskletExecutor[tasks.length];
				for (int i = 0; i < tasklets.length; i++) {
					tasklets[i] = new TaskletExecutor(flowContext, tasks[i]);
					executors.execute(tasklets[i]);
				}
				executors.shutdown();
				try {
					executors.awaitTermination(24, TimeUnit.HOURS);
				} catch (InterruptedException e) {
					logger.error("Failed to wait for subtask to complete.", e);
					System.exit(1);
				}
			}
		} catch (Exception e) {
			if (appContext != null) {
				appContext.close();
			}
			logger.error("Failed to execute tasks", e);
			System.exit(1);
		}
	}

}
