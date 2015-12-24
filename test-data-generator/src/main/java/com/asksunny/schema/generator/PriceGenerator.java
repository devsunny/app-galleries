package com.asksunny.schema.generator;

public class PriceGenerator implements Generator<String> {

	
	
	public PriceGenerator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String nextStringValue() {		
		return nextValue();
	}

	@Override
	public String nextValue() {
		int p1 = RandomUtil.getInstance().getUnsignedInt(100);
		int p2 = RandomUtil.getInstance().getUnsignedInt(100);
		return String.format("%d.%d", p1, p2);
	}

}
