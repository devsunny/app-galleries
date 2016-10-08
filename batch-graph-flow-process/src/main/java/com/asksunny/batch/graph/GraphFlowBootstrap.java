package com.asksunny.batch.graph;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GraphFlowBootstrap {

	private static Logger logger = LoggerFactory.getLogger(GraphFlowBootstrap.class);

	private int maxConcurrentFlow = SystemUtils.getSystemCores();
	public static final String MAX_CONCURRENT_WORKFLOW = "MAX_CONCURRENT_WORKFLOW";

	public GraphFlowBootstrap() {
		init();
	}

	protected void init() {
		String mwf = System.getProperty(MAX_CONCURRENT_WORKFLOW);
		if (mwf == null) {
			mwf = System.getenv(MAX_CONCURRENT_WORKFLOW);
		}
		if (mwf != null) {
			maxConcurrentFlow = Integer.valueOf(mwf);
		}
	}

	protected void executeGraph(String[] args) {

		final ExceptionHolder errHolder = new ExceptionHolder();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				logger.info("Caught unhandled exception in the thread jungle {}:{}", t.getName(), t.getThreadGroup(), e);
				errHolder.setCauseThread(t);
				errHolder.setUncaughtException(e);
			}
		});
		CLIArgumentContext argsmap = new CLIArgumentContext(args);
		AbstractApplicationContext appContext = null;
		try {
			appContext = new ClassPathXmlApplicationContext(args[0].split(","));
			BatchFlowContext flowContext = new BatchFlowContext(appContext, argsmap);
			ExecutorService fixedThreadExecutor = Executors.newFixedThreadPool(this.maxConcurrentFlow);
			String[] tasks = args[1].split(",");
			for (int i = 0; i < tasks.length; i++) {
				WorkflowTask task = appContext.getBean(tasks[i], WorkflowTask.class);
				task.init(flowContext);
				fixedThreadExecutor.execute(task);
			}
			while (flowContext.getNumberOfRunningTasks() > 0) {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					fixedThreadExecutor.shutdown();
				}
			}
			fixedThreadExecutor.shutdown();
		} catch (Exception e) {
			if (appContext != null) {
				appContext.close();
			}
			logger.error("Failed to execute tasks", e);
			System.exit(1);
		}
		if (errHolder.getUncaughtException() != null) {
			throw new RuntimeException("Encountered unhandled exception", errHolder.getUncaughtException());
		}
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

		GraphFlowBootstrap bootstrap = new GraphFlowBootstrap();
		bootstrap.executeGraph(args);
	}

}
