package com.asksunny.schema.generator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;

public class DateGenerator implements Generator<Date> {

	private long minValue;
	private long maxValue;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public DateGenerator(String minValue, String maxValue, String format) {
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

	public DateGenerator(long minValue, long maxValue) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public DateGenerator(Date minValue, Date maxValue) {
		super();
		this.minValue = minValue == null ? 0 : minValue.getTime();
		this.maxValue = maxValue == null ? Long.MAX_VALUE : maxValue.getTime();
	}

	public DateGenerator() {
		this(null, null);
	}

	public String nextStringValue() {
		return sdf.format(nextValue());
	}

	public Date nextValue() {
		return new Date(RandomUtil.getInstance().getRandomLong(this.minValue, this.maxValue));
	}

	public void setFormat(String format) {
		sdf = new SimpleDateFormat(format);
	}

}
