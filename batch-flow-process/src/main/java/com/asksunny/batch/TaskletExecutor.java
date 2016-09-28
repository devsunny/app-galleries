package com.asksunny.batch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

public class TaskletExecutor implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(TaskletExecutor.class);
	private AbstractApplicationContext appContext;
	private String taskletId;
	private BatchFlowContext flowContext;
	private ErrorType errorType = ErrorType.NONE;

	public TaskletExecutor(BatchFlowContext flowContext, String taskletId) {
		setAppContext(flowContext.getAppContext());
		setTaskletId(taskletId);
		setFlowContext(flowContext);
	}

	@Override
	public void run() {
		try {
			Tasklet tasklet = (Tasklet) appContext.getBean(taskletId);
			tasklet.init(getFlowContext());
			String[] nextSteps = tasklet.execute();
			logger.info("Sucessfully executed task:{}", taskletId);
			if (nextSteps == null || nextSteps.length == 0) {
				logger.info("End of process chain");
			} else if (nextSteps.length == 1) {
				TaskletExecutor executor = new TaskletExecutor(flowContext, nextSteps[0]);
				executor.run();
				if (getErrorType() == ErrorType.FATAL) {
					logger.error("Encounter unrecoverable error while executing task:{}", nextSteps[0]);
					System.exit(1);
				} 
			} else {
				ExecutorService executors = Executors.newFixedThreadPool(nextSteps.length);
				TaskletExecutor[] tasklets = new TaskletExecutor[nextSteps.length];
				for (int i = 0; i < tasklets.length; i++) {
					tasklets[i] = new TaskletExecutor(flowContext, nextSteps[i]);					
					executors.execute(tasklets[i]);
				}
				executors.shutdown();
				try {
					executors.awaitTermination(24, TimeUnit.HOURS);
				} catch (InterruptedException e) {
					errorType = ErrorType.FATAL;
					logger.error("Failed to wait for subtask to complete.", e);
				}
				for (int i = 0; i < tasklets.length; i++) {
					if (tasklets[i].getErrorType() == ErrorType.FATAL) {
						logger.error("Encounter unrecoverable error while executing task:{}",
								tasklets[i].getTaskletId());
						System.exit(1);
					} else {
						logger.info("Sucessfully executed task:{}", tasklets[i].getTaskletId());
					}
				}
			}
		} catch (FatalExecutionException t) {
			errorType = ErrorType.FATAL;
			logger.error("Unhandled Exception", t);
		}
		catch (Throwable t) {
			errorType = ErrorType.FATAL;
			logger.error("Unhandled Exception", t);
		}
	}

	public AbstractApplicationContext getAppContext() {
		return appContext;
	}

	public void setAppContext(AbstractApplicationContext appContext) {
		this.appContext = appContext;
	}

	public String getTaskletId() {
		return taskletId;
	}

	public void setTaskletId(String taskletId) {
		this.taskletId = taskletId;
	}

	public BatchFlowContext getFlowContext() {
		return flowContext;
	}

	public void setFlowContext(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
	}

	public ErrorType getErrorType() {
		return errorType;
	}

	public void setErrorType(ErrorType errorType) {
		this.errorType = errorType;
	}

}
