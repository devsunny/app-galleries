package com.asksunny.schema.generator;

import com.asksunny.schema.Field;

public class RefValueGenerator implements Generator<String> {

	private String refValue;
	private Field field;

	public RefValueGenerator(Field field) {
		this.field = field;
	}

	public String getRefValue() {
		return refValue;
	}

	public void setRefValue(String refValue) {
		this.refValue = refValue;
	}

	@Override
	public String nextStringValue() {
		return nextValue();
	}

	@Override
	public String nextValue() {
		return refValue;
	}

}
