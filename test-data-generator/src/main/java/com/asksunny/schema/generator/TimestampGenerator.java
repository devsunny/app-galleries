package com.asksunny.schema.generator;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class TimestampGenerator implements Generator<Timestamp> {

	private long minValue;
	private long maxValue;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public TimestampGenerator(long minValue, long maxValue) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public TimestampGenerator(Date minValue, Date maxValue) {
		super();
		this.minValue = minValue == null ? 0 : minValue.getTime();
		this.maxValue = maxValue == null ? Long.MAX_VALUE : maxValue.getTime();
	}

	public TimestampGenerator() {
		this(null, null);
	}

	public String nextStringValue() {
		return sdf.format(nextValue());
	}

	public Timestamp nextValue() {
		return new Timestamp(RandomUtil.getInstance().getRandomLong(this.minValue, this.maxValue));
	}
	
	public void setFormat(String format)
	{
		sdf = new SimpleDateFormat(format);
	}

}
