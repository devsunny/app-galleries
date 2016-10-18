package com.asksunny.rt.util;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CountBasedSlidingWindowQueue<T extends Object> extends ConcurrentLinkedQueue<T> {

	/**
	 * 
	 */	

	private static final long serialVersionUID = 1L;
	private int maxCount = 1;

	public CountBasedSlidingWindowQueue(int maxCount) {
		super();
		setMaxCount(maxCount);
	}

	public CountBasedSlidingWindowQueue() {
		super();
	}

	public CountBasedSlidingWindowQueue(Collection<? extends T> c) {
		super();
		addAll(c);
	}

	public void fitIntoWindow() {
		while (this.size() > maxCount) {
			this.poll();
		}
	}

	@Override
	public boolean add(T e) {
		boolean ret = super.add(e);
		if (ret) {
			fitIntoWindow();
		}
		return ret;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean ret = super.addAll(c);
		if (ret) {
			fitIntoWindow();
		}
		return super.addAll(c);
	}

	@Override
	public boolean offer(T e) {
		boolean ret = super.offer(e);
		if (ret) {
			fitIntoWindow();
		}
		return ret;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
		fitIntoWindow();
	}

}
