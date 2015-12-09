package com.asksunny.schema.generator;

import com.asksunny.schema.Field;

public class LastNameGenerator implements Generator<String> {

	

	private Field field;

	public LastNameGenerator(Field field) {
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
		}
		return PersonNameUtils.getInstance().getLastName();
	}

}
