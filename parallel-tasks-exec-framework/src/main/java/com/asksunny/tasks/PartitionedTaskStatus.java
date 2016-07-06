package com.asksunny.tasks;

/**
 * 
 * @author SunnyLiu
 *
 */
public class PartitionedTaskStatus {

	private TaskStatus taskStatus = TaskStatus.WAITING;
	private Throwable error;
	private String errorMessage;

	public PartitionedTaskStatus() {
	}

	public PartitionedTaskStatus(TaskStatus taskStatus, String errorMessage, Throwable error) {
		super();
		this.taskStatus = taskStatus;
		this.errorMessage = errorMessage;
		this.error = error;
	}

	public static PartitionedTaskStatus createStatus(TaskStatus taskStatus, String errorMessage, Throwable error) {
		return new PartitionedTaskStatus(taskStatus, errorMessage, error);
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
