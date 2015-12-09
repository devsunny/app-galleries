package com.asksunny.schema.generator;

import com.asksunny.schema.Field;

public class UIntegerGenerator implements Generator<Long> {
	private long minValue;
	private long maxValue;
	private Field field;

	public UIntegerGenerator(Field field) {
		super();
		this.field = field;
		maxValue = field.getMaxValue() == null ? 0 : Integer.valueOf(field.getMaxValue());
		minValue = field.getMinValue() == null ? 0 : Integer.valueOf(field.getMinValue());
		if (minValue < 0) {
			minValue = 0;
		}
	}

	public UIntegerGenerator(int minValue, int maxValue) {
		super();
		if (minValue < 0 || maxValue < 0) {
			throw new IllegalArgumentException("Unsigned int has to be positive value");
		}
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public UIntegerGenerator() {
		this(0, Integer.MAX_VALUE);
	}

	public String nextStringValue() {
		Long out = nextValue();
		return out == null ? null : out.toString();
	}

	public Long nextValue() {
		if (field.isNullable() && RandomUtil.getInstance().isOddEnough()) {
			return null;
		}
		return RandomUtil.getInstance().getUnsignedLong(this.minValue, this.maxValue);
	}

}
