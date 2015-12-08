package com.asksunny.schema.generator;

public class LongGenerator implements Generator<Long> {
	private long minValue;
	private long maxValue;

	public LongGenerator(long minValue, long maxValue) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public LongGenerator() {
		this(Long.MIN_VALUE, Long.MAX_VALUE);
	}

	public String nextStringValue() {
		return String.valueOf(nextValue());
	}

	public Long nextValue() {
		return RandomUtil.getInstance().getRandomLong(this.minValue, this.maxValue);
	}

}
