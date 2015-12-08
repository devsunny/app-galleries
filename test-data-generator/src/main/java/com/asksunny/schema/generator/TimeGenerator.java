package com.asksunny.schema.generator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.Time;

public class TimeGenerator implements Generator<Time> {

	private long minValue;
	private long maxValue;
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	
	public TimeGenerator(String minValue, String maxValue, String format) {
		super();

		if (format != null) {
			this.sdf = new SimpleDateFormat(format);
			try {
				this.minValue = minValue == null ? 0 : this.sdf.parse(minValue).getTime();
				this.maxValue = maxValue == null ? 0 : this.sdf.parse(maxValue).getTime();
			} catch (ParseException e) {
				throw new IllegalArgumentException(String.format("%s %s expect %s", minValue, maxValue, format));
			}
		} else {
			this.minValue = minValue == null ? 0 : Long.valueOf(minValue);
			this.maxValue = maxValue == null ? 0 : Long.valueOf(maxValue);
		}
	}
	
	
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
