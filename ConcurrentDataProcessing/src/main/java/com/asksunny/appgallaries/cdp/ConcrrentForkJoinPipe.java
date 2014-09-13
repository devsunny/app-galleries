package com.asksunny.appgallaries.cdp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcrrentForkJoinPipe implements Pipe {

	private final List<ConcurrentPipe> concurrentPipes = new ArrayList<ConcurrentPipe>();
	private CyclicBarrier cyclicBarrier = null;
	private ExecutorService threadPool = null;
	public ConcrrentForkJoinPipe() {
	}

	public void init() {
		cyclicBarrier = new CyclicBarrier(concurrentPipes.size() + 1);
		threadPool = Executors.newFixedThreadPool(concurrentPipes.size());
		for (ConcurrentPipe pipe : concurrentPipes) {			
			pipe.setProcessBarrier(cyclicBarrier);
			pipe.init();
			threadPool.execute(pipe);
		}
	}

	public void process(String[] data)
	{
		cyclicBarrier.reset();
		for (ConcurrentPipe pipe : concurrentPipes) {
			int[] selection = pipe.getDataSelection();
			if(selection==null){
				pipe.process(data);
			}else{
				String[] subdata = new String[selection.length];
				for (int i = 0; i < subdata.length; i++) {
					subdata[i] = data[selection[i]-1];
				}
				pipe.process(subdata);
			}
		}
		try {
			cyclicBarrier.await();
		} catch (Exception e) {			
			e.printStackTrace();
		} 
		
	}
	
	

	public void endOfDataStream() {
		for (ConcurrentPipe pipe : concurrentPipes) {
			pipe.endOfDataStream();
		}
		threadPool.shutdown();
	}

	public void addPipe(ConcurrentPipe pipe) {
		this.concurrentPipes.add(pipe);
	}

	public void setPipes(List<ConcurrentPipe> pipes) {
		this.concurrentPipes.clear();
		this.concurrentPipes.addAll(pipes);
	}

	public List<ConcurrentPipe> getPipes() {
		return this.concurrentPipes;
	}

}
