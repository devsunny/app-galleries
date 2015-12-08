package com.asksunny.schema.generator;

public class DoubleGenerator implements Generator<Double> {
	private double minValue;
	private double maxValue;

	public DoubleGenerator(double minValue, double maxValue) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public DoubleGenerator() {
		this(Double.MIN_VALUE, Double.MAX_VALUE);
	}

	public String nextStringValue() {
		return String.valueOf(nextValue());
	}

	public Double nextValue() {
		return RandomUtil.getInstance().getRandomDouble(this.minValue, this.maxValue);
	}

}
