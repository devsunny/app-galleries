package com.asksunny.schema;

import java.security.SecureRandom;
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
		return Math.abs(random.nextInt(max))%max;
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
	
	public static void main(String[] args){
		System.out.println(RandomUtil.getInstance().getFormattedString("XDX-DDD-DD-DDDD"));
	}

}
