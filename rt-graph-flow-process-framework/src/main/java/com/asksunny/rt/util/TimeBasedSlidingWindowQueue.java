package com.asksunny.rt.util;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class TimeBasedSlidingWindowQueue<T extends TimeBased> extends ConcurrentLinkedQueue<T> {

	/**
	 * 
	 */
	public static final long MICROSECOND_IN_NANO = 1_000L;
	public static final long MILLISECOND_IN_NANO = 1_000_000L;
	public static final long SECOND_IN_NANO = 1_000_000_000L;
	public static final long MINUTES_IN_NANO = 60 * 1_000_000_000L;
	public static final long HOUR_IN_NANO = 60 * MINUTES_IN_NANO;
	public static final long DAY_IN_NANO = 24 * HOUR_IN_NANO;

	private static final long serialVersionUID = 1L;
	private long windowsInterval = 0;
	private TimeUnit intervalTimeUnit = TimeUnit.SECONDS;
	private TimeBased lastObject = null;
	private long nanoIntervalValue = windowsInterval * SECOND_IN_NANO;

	public TimeBasedSlidingWindowQueue(long windowsInterval, TimeUnit intervalTimeUnit) {
		super();
		this.intervalTimeUnit = intervalTimeUnit;
		setWindowsInterval(windowsInterval);
	}

	public TimeBasedSlidingWindowQueue() {
		super();

	}

	public TimeBasedSlidingWindowQueue(Collection<? extends T> c) {
		super();
		addAll(c);
	}

	public long getWindowsInterval() {
		return windowsInterval;
	}

	public void setWindowsInterval(long windowsInterval) {
		this.windowsInterval = windowsInterval;
		adjustNanoIntervalValue();
	}

	public TimeUnit getIntervalTimeUnit() {
		return intervalTimeUnit;
	}

	public void setIntervalTimeUnit(TimeUnit intervalTimeUnit) {
		this.intervalTimeUnit = intervalTimeUnit;
		adjustNanoIntervalValue();
	}

	private void adjustNanoIntervalValue() {
		switch (intervalTimeUnit) {
		case DAYS:
			nanoIntervalValue = (windowsInterval * DAY_IN_NANO);
			break;
		case HOURS:
			nanoIntervalValue = (windowsInterval * HOUR_IN_NANO);
			break;
		case MINUTES:
			nanoIntervalValue = (windowsInterval * MINUTES_IN_NANO);
			break;
		case SECONDS:
			nanoIntervalValue = (windowsInterval * SECOND_IN_NANO);
			break;
		case MILLISECONDS:
			nanoIntervalValue = (windowsInterval * MILLISECOND_IN_NANO);
			break;
		case MICROSECONDS:
			nanoIntervalValue = (windowsInterval * MICROSECOND_IN_NANO);
			break;
		case NANOSECONDS:
			nanoIntervalValue = (windowsInterval);
			break;
		}
	}

	public void fitIntoWindow() {
		if (lastObject == null || this.isEmpty()) {
			return;
		}
		;
		boolean fit = false;
		do {
			Duration d = Duration.between(this.peek().getTimestamp(), this.lastObject.getTimestamp());
			fit = d.toNanos() <= nanoIntervalValue;
			if (!fit) {
				this.poll();
			}
		} while (!fit && !this.isEmpty());

	}

	@Override
	public boolean add(T e) {
		boolean ret = super.add(e);
		if (ret) {
			this.lastObject = e;
			fitIntoWindow();
		}
		return ret;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean ret = super.addAll(c);
		if (ret && c.size() > 1) {
			this.lastObject = c.stream().skip(c.size() - 1).findFirst().get();
			fitIntoWindow();
		} else if (ret && c.size() == 1) {
			this.lastObject = c.iterator().next();
			fitIntoWindow();
		}
		return super.addAll(c);
	}

	@Override
	public boolean offer(T e) {
		boolean ret = super.offer(e);
		if (ret) {
			this.lastObject = e;
			fitIntoWindow();
		}
		return ret;
	}

}
