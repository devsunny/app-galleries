package com.asksunny.schema.generator;

import com.asksunny.schema.Field;

public class FirstNameGenerator implements Generator<String> {

	private Field field;

	public FirstNameGenerator(Field field) {
		super();
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
		}else{
			return PersonNameUtils.getInstance().getFirstName();
		}
	}

}
