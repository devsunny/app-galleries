package com.asksunny.schema.generator;

public class ULongGenerator implements Generator<Long> {
	private long minValue;
	private long maxValue;

	public ULongGenerator(long minValue, long maxValue) {
		super();
		if(minValue<0 || maxValue<0){
			throw new IllegalArgumentException("Unsigned long has to be positive value");
		}
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public ULongGenerator() {
		this(0L, Long.MAX_VALUE);
	}

	public String nextStringValue() {
		return String.valueOf(nextValue());
	}

	public Long nextValue() {
		return RandomUtil.getInstance().getRandomLong(this.minValue, this.maxValue);
	}

}
