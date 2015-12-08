package com.asksunny.schema.generator;

public class IntegerGenerator implements Generator<Integer> {
	private int minValue;
	private int maxValue;

	public IntegerGenerator(int minValue, int maxValue) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public IntegerGenerator() {
		this(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public String nextStringValue() {
		return String.valueOf(nextValue());
	}

	public Integer nextValue() {
		return RandomUtil.getInstance().getRandomInt(this.minValue, this.maxValue);
	}

}
