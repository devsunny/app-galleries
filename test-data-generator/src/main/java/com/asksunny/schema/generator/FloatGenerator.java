package com.asksunny.schema.generator;

public class FloatGenerator implements Generator<Float> {
	private float minValue;
	private float maxValue;

	public FloatGenerator(float minValue, float maxValue) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public FloatGenerator() {
		this(Float.MIN_VALUE, Float.MAX_VALUE);
	}

	public String nextStringValue() {
		return String.valueOf(nextValue());
	}

	public Float nextValue() {
		return RandomUtil.getInstance().getRandomFloat(this.minValue, this.maxValue);
	}

}
