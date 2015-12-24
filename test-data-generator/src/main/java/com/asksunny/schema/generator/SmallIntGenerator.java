package com.asksunny.schema.generator;

public class SmallIntGenerator implements Generator<String> {

	public SmallIntGenerator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String nextStringValue() {
		return nextValue();
	}

	@Override
	public String nextValue() {
		return Integer.toString(RandomUtil.getInstance().getUnsignedInt(Short.MAX_VALUE));
	}

}
