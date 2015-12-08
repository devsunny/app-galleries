package com.asksunny.schema.generator;

public class StateGenerator implements Generator<String> {

	AddressHolder addressHolder;

	public StateGenerator(AddressHolder addressHolder) {
		super();
		this.addressHolder = addressHolder;
		addressHolder.registerToUse();
	}

	@Override
	public String nextStringValue() {		
		return nextValue();
	}

	@Override
	public String nextValue() {
		return addressHolder.getAddress().getState();
	}

}
