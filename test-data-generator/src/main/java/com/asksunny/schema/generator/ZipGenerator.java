package com.asksunny.schema.generator;

public class ZipGenerator implements Generator<String> {

	AddressHolder addressHolder;

	public ZipGenerator(AddressHolder addressHolder) {
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
		return addressHolder.getAddress().getZip();
	}

}
