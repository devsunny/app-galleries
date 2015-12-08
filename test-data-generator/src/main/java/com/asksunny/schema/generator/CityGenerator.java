package com.asksunny.schema.generator;

public class CityGenerator implements Generator<String> {

	AddressHolder addressHolder;

	public CityGenerator(AddressHolder addressHolder) {
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
		return addressHolder.getAddress().getCity();
	}

}
