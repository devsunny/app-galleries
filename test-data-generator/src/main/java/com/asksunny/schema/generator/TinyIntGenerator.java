package com.asksunny.schema.generator;

public class TinyIntGenerator implements Generator<String> {

	public TinyIntGenerator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String nextStringValue() {
		return nextValue();
	}

	@Override
	public String nextValue() {
		return Integer.toString(RandomUtil.getInstance().getUnsignedInt(256));
	}

}
