package com.asksunny.schema.generator;

import java.math.BigDecimal;
import java.sql.Types;

import com.asksunny.schema.Field;

public class DoubleGenerator implements Generator<BigDecimal> {
	private double maxValue;
	private double minValue;
	private int precision;
	private int scale;
	private Field field;
	private long intDigitsMax = 0L;
	private long decimalDigitsMax = 0L;

	public DoubleGenerator(Field field) {

		this.field = field;
		this.precision = field.getPrecision();
		this.scale = field.getScale();

		this.maxValue = field.getMaxValue() == null ? 0 : Double.valueOf(field.getMaxValue());
		this.minValue = field.getMinValue() == null ? 0 : Double.valueOf(field.getMinValue());

		if (this.maxValue != 0) {
			this.precision = Long.toString((long) this.maxValue).length() + this.scale-1;
		}
		if (this.minValue != 0) {
			String text = Double.toString((double) this.minValue);
			int idx = text.indexOf(".");
			if (idx == -1) {
				this.scale = 2;
			} else {
				this.scale = text.length() - idx - 1;
			}
		}
		if (this.precision == 0) {
			switch (field.getJdbcType()) {
			case Types.DOUBLE:
				this.precision = 10;
				this.scale = 4;
				break;
			case Types.FLOAT:
				this.precision = 6;
				this.scale = 2;
				break;
			case Types.DECIMAL:
			case Types.REAL:
			default:
				this.precision = 8;
				this.scale = 4;
				break;
			}
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
