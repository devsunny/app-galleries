package com.asksunny.schema.generator;

public class TextGenerator implements Generator<String> {

	private int size;
	private boolean nullable;

	public TextGenerator(int size, boolean nullable) {
		super();
		this.size = size;
		this.nullable = true;
	}

	@Override
	public String nextStringValue() {
		return nextValue();
	}

	@Override
	public String nextValue() {
		String ret = TextUtils.getInstance().getText(0, size);
		if (ret.length() == 0 || ret.length() % 13 == 0) {
			return null;
		} else {
			return ret;
		}
	}

}
