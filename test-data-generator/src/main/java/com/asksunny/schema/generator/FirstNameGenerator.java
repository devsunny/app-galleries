package com.asksunny.schema.generator;

public class FirstNameGenerator implements Generator<String> {

	private boolean nullable;

	public FirstNameGenerator(boolean nullable) {
		super();
		this.nullable = nullable;
	}

	public FirstNameGenerator() {
		super();
		this.nullable = false;
	}

	@Override
	public String nextStringValue() {
		return nextValue();
	}

	@Override
	public String nextValue() {
		if (nullable && RandomUtil.getInstance().getUnsignedInt(100000) % 13 == 0) {
			return null;
		}
		return PersonNameUtils.getInstance().getFirstName();
	}

}
