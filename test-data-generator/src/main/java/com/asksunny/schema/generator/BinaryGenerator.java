package com.asksunny.schema.generator;

import java.nio.charset.Charset;

public class BinaryGenerator implements Generator<byte[]> {

	private int minValue;
	private int maxValue;

	public BinaryGenerator(int minValue, int maxValue) {
		if (minValue < 0 || maxValue < 0) {
			throw new IllegalArgumentException("Unsigned int has to be positive value");
		}
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public BinaryGenerator(int maxValue) {
		if (maxValue < 0) {
			throw new IllegalArgumentException("Unsigned int has to be positive value");
		}
		this.minValue = 0;
		this.maxValue = maxValue;
	}

	public BinaryGenerator() {
		this.minValue = 0;
		this.maxValue = 1024;
	}

	public String nextStringValue() {
		return new String(nextValue(), Charset.defaultCharset()); // should it
																	// be
																	// base64?
	}

	public byte[] nextValue() {
		int len = this.minValue + RandomUtil.getInstance().getUnsignedInt(this.maxValue - this.minValue);
		byte[] buf = new byte[len];
		RandomUtil.getInstance().getRandom().nextBytes(buf);
		return buf;
	}

}
