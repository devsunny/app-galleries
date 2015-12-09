package com.asksunny.schema.generator;

import java.math.BigDecimal;
import java.sql.Types;

import com.asksunny.schema.Field;

public class DoubleGenerator implements Generator<BigDecimal> {
	private double maxValue;
	private int precision;
	private int scale;
	private Field field;
	private long intDigitsMax = 0L;
	private long decimalDigitsMax = 0L;

	public DoubleGenerator(Field field) {
		this.field = field;
		this.precision = this.field.getPrecision();
		this.maxValue = field.getMaxValue() == null ? 0 : Double.valueOf(field.getMaxValue());
		if (this.precision == 0 && this.maxValue != 0) {
			this.precision = Long.toString((long) this.maxValue).length();
		}
		if (this.precision == 0) {
			switch (field.getJdbcType()) {
			case Types.DOUBLE:
				int ilen = Long.toString((long) Double.MAX_VALUE).length();
				this.scale = Double.toString(Double.MAX_VALUE).length() - ilen;
				this.precision = ilen + scale;
				break;
			case Types.FLOAT:
				int filen = Long.toString((long) Float.MAX_VALUE).length();
				this.scale = Float.toString(Float.MAX_VALUE).length() - filen;
				this.precision = filen + scale;
				break;
			case Types.DECIMAL:
			case Types.REAL:
			default:
				this.precision = 6;
				this.scale = 2;
				break;
			}
		}
		if (this.maxValue == 0) {
			this.maxValue = (double) Math.pow(10, this.precision - this.scale);
		}
		decimalDigitsMax = (long) Math.pow(10, this.scale);
		intDigitsMax = (long) Math.pow(10, this.precision - this.scale);

	}

	public String nextStringValue() {
		BigDecimal out = nextValue();

		return out == null ? null : out.toPlainString();
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
