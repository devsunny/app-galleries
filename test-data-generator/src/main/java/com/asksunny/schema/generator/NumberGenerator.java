package com.asksunny.schema.generator;

import java.math.BigDecimal;

import com.asksunny.schema.Field;

public class NumberGenerator implements Generator<BigDecimal> {

	private int precision;
	private int scale;

	private long intDigitsMax = 0L;
	private long decimalDigitsMax = 0L;
	private Field field;

	public NumberGenerator(Field field) {
		super();
		this.precision = field.getPrecision();
		this.scale = field.getScale();
		this.field = field;
		decimalDigitsMax = (long) Math.pow(10, this.scale);
		intDigitsMax = (long) Math.pow(10, this.precision - this.scale);

	}

	public String nextStringValue() {
		BigDecimal out = nextValue();
		return out == null ? null : nextValue().toPlainString();
	}

	public BigDecimal nextValue() {

		if (field.isNullable() && RandomUtil.getInstance().isOddEnough()) {
			return null;
		}

		if (this.scale > 0) {
			return new BigDecimal(String.format("%d.%d", RandomUtil.getInstance().getUnsignedLong(intDigitsMax),
					RandomUtil.getInstance().getUnsignedLong(decimalDigitsMax)));
		} else {
			return new BigDecimal(RandomUtil.getInstance().getUnsignedLong(intDigitsMax));

		}
	}

}
