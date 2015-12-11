package com.asksunny.app.domain;

public class Header 
{
	private int runningJobs;
	private int watingJobs;
	private int notfiyingJobs;
	private int readyJobs;
	
	public int getRunningJobs() {
		return runningJobs;
	}
	public void setRunningJobs(int runningJobs) {
		this.runningJobs = runningJobs;
	}
	public int getWatingJobs() {
		return watingJobs;
	}
	public void setWatingJobs(int watingJobs) {
		this.watingJobs = watingJobs;
	}
	public int getNotfiyingJobs() {
		return notfiyingJobs;
	}
	public void setNotfiyingJobs(int notfiyingJobs) {
		this.notfiyingJobs = notfiyingJobs;
	}
	public int getReadyJobs() {
		return readyJobs;
	}
	public void setReadyJobs(int readyJobs) {
		this.readyJobs = readyJobs;
	}
	
	
	
}
