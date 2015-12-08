package com.asksunny.schema.generator;

public class UFloatGenerator implements Generator<Float> {
	private float minValue;
	private float maxValue;

	public UFloatGenerator(float minValue, float maxValue) {
		super();
		if(minValue<0 || maxValue<0){
			throw new IllegalArgumentException("Unsigned float has to be positive value");
		}
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public UFloatGenerator() {
		this(0, Float.MAX_VALUE);
	}

	public String nextStringValue() {
		return String.valueOf(nextValue());
	}

	public Float nextValue() {
		return RandomUtil.getInstance().getRandomFloat(this.minValue, this.maxValue);
	}

}
