package com.asksunny.appgallaries.cdp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConcurrentPipeline implements ConcurrentPipe {

	private final AtomicBoolean endOfDataStream = new AtomicBoolean(false);
	private CyclicBarrier processBarrier;
	private String[] currentData = null;
	private final List<Pipe> pipes = new ArrayList<Pipe>();
	public int[] dataSelection = null;

	public ConcurrentPipeline() {

	}

	public ConcurrentPipeline(CyclicBarrier processBarrier) {
		this.processBarrier = processBarrier;
	}

	public void init() {

	}

	public ConcurrentPipeline addPipe(Pipe pipe) {
		this.pipes.add(pipe);
		return this;
	}

	public void setPipes(List<Pipe> pipes) {
		this.pipes.clear();
		this.pipes.addAll(pipes);
	}

	public List<Pipe> getPipes() {
		return pipes;
	}

	public void process(String[] data) {
		this.currentData = data;
	}

	public void endOfDataStream() {
		this.endOfDataStream.set(true);
	}

	public CyclicBarrier getProcessBarrier() {
		return processBarrier;
	}

	public void setProcessBarrier(CyclicBarrier processBarrier) {
		this.processBarrier = processBarrier;
	}

	public void run() {

		while (endOfDataStream.get() == false || currentData != null) {
			if (currentData != null) {
				for (Pipe pipe : pipes) {
					pipe.process(currentData);
				}
				currentData = null;
				try {
					processBarrier.await();
				} catch (Exception e) {
					;
				}
			}

		}

	}

	public int[] getDataSelection() {
		return dataSelection;
	}

	public void setDataSelection(int[] dataSelection) {
		this.dataSelection = dataSelection;		
	}

}
