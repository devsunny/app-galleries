package com.asksunny.schema.generator;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public final class RandomUtil {

	public static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	public static final char[] UPPER_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	public static final char[] LOWER_LETTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	public static final char[] DIGITS = "0123456789".toCharArray();
	private static RandomUtil instance = new RandomUtil();
	private final SecureRandom random;

	private RandomUtil() {
		random = new SecureRandom(UUID.randomUUID().toString().getBytes());
	}

	public char getDigit() {

		int idx = Math.abs(random.nextInt(100)) % DIGITS.length;
		return DIGITS[idx];
	}

	public char getLetter() {

		int idx = Math.abs(random.nextInt(100)) % LETTERS.length;
		return LETTERS[idx];
	}

	public char getUpperLetter() {

		int idx = Math.abs(random.nextInt(100)) % UPPER_LETTERS.length;
		return UPPER_LETTERS[idx];
	}

	public char getLowerLetter() {

		int idx = Math.abs(random.nextInt(100)) % LOWER_LETTERS.length;
		return LOWER_LETTERS[idx];
	}

	public int getUnsignedInt(int max) {
		return Math.abs(random.nextInt(max)) % max;
	}

	public long getUnsignedLong(long max) {
		long l = random.nextLong() & 0x7FFFFFFFFFFFFFFFL;
		return l % max;
	}

	public long getUnsignedLong(long min, long max) {
		long l = getUnsignedLong(max);
		if (l < min) {
			l = min + (l % (max - min));
		}
		return l;
	}
	
	public boolean isOddEnough()
	{
		return getUnsignedInt(10000)%13==0;
	}

	public int getRandomInt(int min, int max) {
		if (max == Integer.MAX_VALUE) {
			return min + (Math.abs(random.nextInt(Integer.MAX_VALUE)) & 0x7FFFFFFF);
		} else {
			return min + Math.abs(random.nextInt(((max - min) & 0x7FFFFFFF)));
		}

	}

	public Random getRandom() {
		return this.random;
	}

	public long getRandomLong(long min, long max) {

		long rlong = random.nextLong();
		if (rlong >= min && rlong <= max) {
			return rlong;
		} else if (rlong > max) {
			return rlong % max;
		} else {
			return (min + rlong) % max;
		}

	}

	public double getRandomDouble(double min, double max) {
		double rlong = random.nextDouble();
		if (rlong >= min && rlong <= max) {
			return rlong;
		} else if (rlong > max) {
			return rlong % max;
		} else {
			return (min + rlong) % max;
		}

	}

	public float getRandomFloat(float min, float max) {
		float rlong = random.nextFloat();
		if (rlong >= min && rlong <= max) {
			return rlong;
		} else if (rlong > max) {
			return rlong % max;
		} else {
			return (min + rlong) % max;
		}

	}

	public String getFormattedString(String format) {
		StringBuilder buf = new StringBuilder();
		int len = format.length();
		for (int i = 0; i < len; i++) {
			char c = format.charAt(i);
			if (c == 'X') {
				buf.append(getUpperLetter());
			} else if (c == 'x') {
				buf.append(getLowerLetter());
			} else if (c == 'D' || c == 'd') {
				buf.append(getDigit());
			} else {
				buf.append(c);
			}
		}
		return buf.toString();
	}

	public static RandomUtil getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		System.out.println(RandomUtil.getInstance().getFormattedString("XDX-DDD-DD-DDDD"));

		long t = 1L;
		long x = t & 0x7FFFFFFFFFFFFFFFL;
		System.out.println(x);
	}

}
