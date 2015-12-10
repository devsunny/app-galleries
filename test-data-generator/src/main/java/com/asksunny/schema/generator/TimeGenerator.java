package com.asksunny.schema.generator;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.asksunny.schema.Field;

public class TimeGenerator implements Generator<Time> {

	private long minValue;
	private long maxValue;
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	
	private Field dateField;

	public TimeGenerator(Field dateField) {
		super();
		this.dateField = dateField;
		if (dateField.getFormat() != null) {
			this.sdf = new SimpleDateFormat(dateField.getFormat());
			try {
				this.minValue = dateField.getMinValue() == null ? 0 : this.sdf.parse(dateField.getMinValue()).getTime();
				this.maxValue = dateField.getMaxValue() == null ? System.currentTimeMillis() : this.sdf.parse(dateField.getMaxValue()).getTime();
			} catch (ParseException e) {
				throw new IllegalArgumentException(
						String.format("%s %s expect %s", minValue, maxValue, dateField.getFormat()));
			}
		} else {
			this.minValue = dateField.getMinValue() == null ? 0 : Long.valueOf(minValue);
			this.maxValue = dateField.getMaxValue() == null ? System.currentTimeMillis() : Long.valueOf(maxValue);
		}
	}

	public String nextStringValue() {
		Time out = nextValue();
		return out != null ? sdf.format(nextValue()) : null;
	}

	public Time nextValue() {
		if (this.dateField.isNullable() && RandomUtil.getInstance().isOddEnough()) {
			return null;
		}
		return new Time(RandomUtil.getInstance().getRandomLong(this.minValue, this.maxValue));
	}
	
	public void setFormat(String format)
	{
		sdf = new SimpleDateFormat(format);
	}

}
