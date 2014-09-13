package com.asksunny.appgallaries.cdp;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentDataProcessor implements Runnable {

	private final AtomicBoolean endOfDataStream = new AtomicBoolean(false);
	private final AtomicLong numberOfRecords = new AtomicLong(0);	
	private CountDownLatch readyLatch;
	private CyclicBarrier processBarrier;
	private ConcurrentLinkedQueue<String[]> datain = new ConcurrentLinkedQueue<String[]>();

	public ConcurrentDataProcessor(final CountDownLatch readyLatch, final CyclicBarrier processBarrier) {
		this.processBarrier = processBarrier;
		this.readyLatch = readyLatch;
	}

	public void init() {

	}

	public void run() {
		readyLatch.countDown();
		while (endOfDataStream.get() == false
				||  numberOfRecords.get() >0) {
			if ( numberOfRecords.get() > 0 ) {
				String[] data = datain.isEmpty() ? null : datain.remove();
				if (data != null) {
					// Process data here	
					System.out.println(data[0]);
					//Simulate data process here
					try {
						Thread.sleep(1*1000);
					} catch (InterruptedException e1) {
						;
					}
					
					numberOfRecords.decrementAndGet();
					try {
						processBarrier.await();
					} catch (Exception e) {
						;
					} 
				}
				
			}

		}
	}

	public void sendData(String[] data) 
	{
		datain.add(data);
		numberOfRecords.incrementAndGet();
	}
	
	public void endOfDataStream()
	{
		this.endOfDataStream.set(true);
	}

}
