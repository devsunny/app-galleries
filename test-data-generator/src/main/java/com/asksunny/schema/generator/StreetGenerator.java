package com.asksunny.schema.generator;

public class StreetGenerator implements Generator<String> {

	AddressHolder addressHolder;

	public StreetGenerator(AddressHolder addressHolder) {
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
		return addressHolder.getAddress().getStreet();
	}

}
