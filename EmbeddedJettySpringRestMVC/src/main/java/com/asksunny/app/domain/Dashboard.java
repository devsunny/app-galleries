package com.asksunny.app.domain;

import java.util.List;

public class Dashboard 
{

	private Header header;
	private List<Task> watingTasks;
	private List<Task> readyTasks;
	private List<Task> notifyingTasks;
	private List<Task> runningTasks;
	
	
	
	
	public Dashboard(Header header, List<Task> watingTasks, List<Task> readyTasks, List<Task> notifyingTasks,
			List<Task> runningTasks) {
		super();
		this.header = header;
		this.watingTasks = watingTasks;
		this.readyTasks = readyTasks;
		this.notifyingTasks = notifyingTasks;
		this.runningTasks = runningTasks;
	}
	public Dashboard() {
		super();		
	}
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public List<Task> getWatingTasks() {
		return watingTasks;
	}
	public void setWatingTasks(List<Task> watingTasks) {
		this.watingTasks = watingTasks;
	}
	public List<Task> getReadyTasks() {
		return readyTasks;
	}
	public void setReadyTasks(List<Task> readyTasks) {
		this.readyTasks = readyTasks;
	}
	public List<Task> getNotifyingTasks() {
		return notifyingTasks;
	}
	public void setNotifyingTasks(List<Task> notifyingTasks) {
		this.notifyingTasks = notifyingTasks;
	}
	public List<Task> getRunningTasks() {
		return runningTasks;
	}
	public void setRunningTasks(List<Task> runningTasks) {
		this.runningTasks = runningTasks;
	}
	
	
	
}
