package com.asksunny.schema.generator;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

public class ForeignKeyFieldGenerator implements Generator<String> {

	protected static SecureRandom rand = new SecureRandom(UUID.randomUUID().toString().getBytes());
	private List<String> values = null;

	public ForeignKeyFieldGenerator(List<String> values) {
		this.values = values;
	}

	@Override
	public String nextStringValue() {
		return nextValue();
	}

	@Override
	public String nextValue() {
		if (this.values == null || this.values.size() == 0) {
			return null;
		}
		int idx = Math.abs(rand.nextInt(this.values.size())) % this.values.size();
		return this.values.get(idx);
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public ForeignKeyFieldGenerator() {
		super();		
	}

}
