package com.asksunny.schema.generator;

import com.asksunny.schema.Field;

public class TextGenerator implements Generator<String> {

	private int size;
	private Field field;

	public TextGenerator(Field field) {
		super();
		this.field = field;
		this.size = field.getDisplaySize();
		if (this.size == 0) {
			this.size = field.getPrecision();
		}		
		if (size == 0) {
			size = 8;
		}
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
		
		int gsize = field.getMaxValue()!=null?Integer.valueOf(field.getMaxValue()):size;
		
		String ret = TextUtils.getInstance().getText(0, gsize);
		if (ret.length() == 0 || ret.length() % 13 == 0) {
			return null;
		} else {
			return ret;
		}
	}

}
