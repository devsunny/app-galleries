package com.asksunny.framework;

public interface ServerService 
{
	public long startTime();
	public long uptime();
	public void pause();
	public void resume();
	public void stop();
	public void shutdownNow();
}
