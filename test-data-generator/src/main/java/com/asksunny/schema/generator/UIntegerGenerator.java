package com.asksunny.schema.generator;

public class UIntegerGenerator implements Generator<Integer> {
	private int minValue;
	private int maxValue;

	public UIntegerGenerator(int minValue, int maxValue) {
		super();
		if(minValue<0 || maxValue<0){
			throw new IllegalArgumentException("Unsigned int has to be positive value");
		}
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public UIntegerGenerator() {
		this(0, Integer.MAX_VALUE);
	}

	public String nextStringValue() {
		return String.valueOf(nextValue());
	}

	public Integer nextValue() {
		return RandomUtil.getInstance().getRandomInt(this.minValue, this.maxValue);
	}

}
