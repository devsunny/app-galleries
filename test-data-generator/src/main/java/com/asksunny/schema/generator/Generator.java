package com.asksunny.schema.generator;

public interface Generator<T> {
	public String nextStringValue();
	public T nextValue();
	
}
