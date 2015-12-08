package com.asksunny.schema.generator;

import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.Time;

public class TimeGenerator implements Generator<Time> {

	private long minValue;
	private long maxValue;
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	public TimeGenerator(long minValue, long maxValue) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public TimeGenerator(Date minValue, Date maxValue) {
		super();
		this.minValue = minValue == null ? 0 : minValue.getTime();
		this.maxValue = maxValue == null ? Long.MAX_VALUE : maxValue.getTime();
	}

	public TimeGenerator() {
		this(null, null);
	}

	public String nextStringValue() {
		return sdf.format(nextValue());
	}

	public Time nextValue() {
		return new Time(RandomUtil.getInstance().getRandomLong(this.minValue, this.maxValue));
	}
	
	public void setFormat(String format)
	{
		sdf = new SimpleDateFormat(format);
	}

}
