package com.asksunny.schema.generator;

import java.math.BigDecimal;

public class NumberGenerator implements Generator<BigDecimal> {

	private int precision;
	private int scale;

	private long intDigitsMax = 0L;
	private long decimalDigitsMax = 0L;

	public NumberGenerator(int precision, int scale) {
		super();
		this.precision = precision;
		this.scale = scale;
		decimalDigitsMax = (long) Math.pow(10, this.scale);
		intDigitsMax = (long) Math.pow(10, this.precision - this.scale);
	}

	public NumberGenerator() {
		this(16, 0);
	}

	public String nextStringValue() {
		return nextValue().toPlainString();
	}

	public BigDecimal nextValue() {
		if (this.scale > 0) {
			return new BigDecimal(String.format("%d.%d", RandomUtil.getInstance().getUnsignedLong(intDigitsMax),
					RandomUtil.getInstance().getUnsignedLong(decimalDigitsMax)));
		} else {
			return new BigDecimal(RandomUtil.getInstance().getUnsignedLong(intDigitsMax));

		}
	}

	public static void main(String[] args) {
		NumberGenerator g = new NumberGenerator(5, 2);
		System.out.println(g.nextStringValue());
	}

}
