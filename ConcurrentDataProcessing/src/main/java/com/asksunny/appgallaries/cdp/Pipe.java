package com.asksunny.appgallaries.cdp;

public interface Pipe 
{
	public void init();
	public void process(String[] data);
	public void endOfDataStream();
}
