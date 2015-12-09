package com.asksunny.schema.generator;

import com.asksunny.schema.Field;

public class TextGenerator implements Generator<String> {

	private int size;
	private Field field;

	public TextGenerator(Field field) {
		super();
		this.size = field.getDisplaySize();
		this.field = field;
	}

	@Override
	public String nextStringValue() {
		return nextValue();
	}

	@Override
	public String nextValue() {
		if (field.isNullable() && RandomUtil.getInstance().isOddEnough()) {
			return null;
		}
		String ret = TextUtils.getInstance().getText(0, size);
		if (ret.length() == 0 || ret.length() % 13 == 0) {
			return null;
		} else {
			return ret;
		}
	}

}
