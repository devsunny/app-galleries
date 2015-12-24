package com.asksunny.schema.generator;

public class LuxuryPriceGenerator implements Generator<String> {

	
	
	public LuxuryPriceGenerator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String nextStringValue() {		
		return nextValue();
	}

	@Override
	public String nextValue() {
		int p1 = RandomUtil.getInstance().getUnsignedInt(10000);
		int p2 = RandomUtil.getInstance().getUnsignedInt(100);
		return String.format("%d.%d", p1, p2);
	}

}
