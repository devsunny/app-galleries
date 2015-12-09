package com.asksunny.schema.generator;

import com.asksunny.schema.Field;

public class StateGenerator implements Generator<String> {

	AddressHolder addressHolder;
	private Field field;
	
	public StateGenerator( Field field, AddressHolder addressHolder) {
		super();
		this.field = field;
		this.addressHolder = addressHolder;
		addressHolder.registerToUse();
	}

	@Override
	public String nextStringValue() {		
		return nextValue();
	}

	@Override
	public String nextValue() {
		if(this.field.isNullable() && RandomUtil.getInstance().isOddEnough()){
			return null;
		}
		return addressHolder.getAddress().getState();
	}

}
