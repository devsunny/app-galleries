package com.asksunny.appgallaries.cdp;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentDataDistributor 
{

	
	
	public ConcurrentDataDistributor() {
		
	}
	
	public static void main(String[] args)
	{
		
		int concurrency  = 4;
        ExecutorService executor = Executors.newFixedThreadPool(4);
        ConcurrentDataProcessor[] processors = new ConcurrentDataProcessor[concurrency];
        CountDownLatch readylatch = new CountDownLatch(concurrency);
        CyclicBarrier barrier = new CyclicBarrier(concurrency+1);
        for(int i=0; i<4; i++)
        {
        	ConcurrentDataProcessor p = new ConcurrentDataProcessor(readylatch, barrier);
        	processors[i] = p;
        	executor.execute(p);
        }
        
        try {
        	readylatch.await();
		} catch (InterruptedException e) {
			;
		}
        
        for(int j=0; j<2; j++){  
        	barrier.reset();
        	String[] data = {String.format("%06d", j), "col2", "col3", "col4", "col5", "col6"};        	
        	for(int i=0; i<concurrency; i++){
        		processors[i].sendData(data);
        	}
        	try {
				barrier.await();
			} catch (Exception e) {				
				e.printStackTrace();
			} 
        }          
        
        
        
        for(int i=0; i<concurrency; i++)
        {        
        	processors[i].endOfDataStream();        	       	
        }
        executor.shutdown();
        
       
	}
	
	

}
