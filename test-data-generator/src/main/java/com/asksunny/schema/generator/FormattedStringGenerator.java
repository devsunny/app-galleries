package com.asksunny.schema.generator;

import com.asksunny.schema.Field;

public class FormattedStringGenerator implements Generator<String> {

	private String format;
	private Field fmtField;

	public FormattedStringGenerator(Field fmtField) {
		super();
		this.format = fmtField.getFormat();
		this.fmtField = fmtField;
	}

	@Override
	public String nextStringValue() {
		return nextValue();
	}

	@Override
	public String nextValue() {
		if (this.fmtField.isNullable() && RandomUtil.getInstance().isOddEnough()) {
			return null;
		}
		return RandomUtil.getInstance().getFormattedString(format);
	}

}
