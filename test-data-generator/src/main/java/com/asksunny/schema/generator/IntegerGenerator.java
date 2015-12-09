package com.asksunny.schema.generator;

import java.sql.Types;

import com.asksunny.schema.Field;

public class IntegerGenerator implements Generator<Long> {
	
	private long minValue;
	private long maxValue;
	private int precision;
	private Field field;

	public IntegerGenerator(Field field) {
		this.field = field;
		this.precision = this.field.getPrecision();		
		this.minValue = field.getMinValue() == null ? 0 : Long.valueOf(field.getMinValue());
		this.maxValue = field.getMaxValue() == null ? 0 : Long.valueOf(field.getMaxValue());
		if (this.precision == 0 && this.maxValue != 0) {
			this.precision = Long.toString(this.maxValue).length();
		}
		switch (field.getJdbcType()) {
		case Types.INTEGER:
			this.precision = Integer.toString(Integer.MAX_VALUE).length();
			break;
		case Types.BIGINT:
			this.precision = Long.toString(Long.MAX_VALUE).length();
			break;
		case Types.SMALLINT:
			this.precision = Integer.toString(Short.MAX_VALUE).length();
			break;
		case Types.TINYINT:
			this.precision = Integer.toString(Byte.MAX_VALUE).length();
			break;
		default:
			this.precision = 4;
			break;
		}
		if (this.maxValue == 0) {
			this.maxValue = (long) Math.pow(10, this.precision);
		}

	}

	public String nextStringValue() {
		Long out = nextValue();
		return out == null ? null : String.valueOf(nextValue());
	}

	public Long nextValue() {
		if ((this.field.isNullable() && RandomUtil.getInstance().isOddEnough())) {
			return null;
		}
		return RandomUtil.getInstance().getRandomLong(minValue, maxValue);
	}

}
