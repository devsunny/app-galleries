package com.asksunny.schema.generator;

public class UDoubleGenerator implements Generator<Double> {
	private double minValue;
	private double maxValue;

	public UDoubleGenerator(double minValue, double maxValue) {
		super();
		if(minValue<0 || maxValue<0){
			throw new IllegalArgumentException("Unsigned double has to be positive value");
		}
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public UDoubleGenerator() {
		this(0, Double.MAX_VALUE);
	}

	public String nextStringValue() {
		return String.valueOf(nextValue());
	}

	public Double nextValue() {
		return RandomUtil.getInstance().getRandomDouble(this.minValue, this.maxValue);
	}

}
