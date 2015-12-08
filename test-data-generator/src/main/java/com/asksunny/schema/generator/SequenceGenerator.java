package com.asksunny.schema.generator;

import java.math.BigInteger;

public class SequenceGenerator {
	private BigInteger sequence;
	private BigInteger step = new BigInteger("1");

	public SequenceGenerator(String base, String step) {
		this.sequence = new BigInteger(base);
		this.step = new BigInteger(step);
	}

	public SequenceGenerator() {
		this.sequence = new BigInteger("1");
		this.step = new BigInteger("1");
	}

	public SequenceGenerator(int base, int step) {
		this.sequence = new BigInteger(String.valueOf(base));
		this.step = new BigInteger(String.valueOf(step));
	}

	public String nextValue() {
		String ret = sequence.toString();
		sequence = sequence.add(step);
		return ret;
	}
	
	

	public static void main(String[] args) {
		SequenceGenerator gen = new SequenceGenerator(10, 3);
		for (int i = 0; i < 200; i++)
			System.out.println(gen.nextValue());
	}

}
