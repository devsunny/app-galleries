package com.asksunny.appgallaries.cdp;

import java.util.concurrent.CyclicBarrier;


public interface ConcurrentPipe extends Runnable, Pipe {
	public int[] getDataSelection();
	public void setProcessBarrier(CyclicBarrier processBarrier);
}
