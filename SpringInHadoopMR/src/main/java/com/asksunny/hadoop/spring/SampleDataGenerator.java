package com.asksunny.hadoop.spring;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SampleDataGenerator {
	

	public final static char[] ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_- "
			.toCharArray();
	public final static char[] ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();
	public final static char[] NUMERIC = "1234567890".toCharArray();

	private SecureRandom random = null;

	public SampleDataGenerator() {
		random = new SecureRandom(new Date().toString().getBytes());
	}

	protected int genInt(int max) {
		return random.nextInt(max);
	}

	protected int genInt(int min, int max) {
		int ret = random.nextInt(max - min) + min;
		return ret;
	}

	protected int genPositiveInt(int max) {
		int ret = random.nextInt(max);
		if (ret < 0 && max != 0) {
			ret = Math.abs(ret) % max;
		}
		return ret;
	}

	protected int genPositiveInt(int min, int max) {
		int ret = genPositiveInt(max - min) + min;
		return ret;
	}

	protected long genLong(long max) {
		return random.nextLong() % max;
	}

	protected long genLong(long min, long max) {
		return (random.nextLong() % max) + min;
	}

	protected long genPositiveLong(long max) {
		long ret = random.nextLong() % max;
		if (ret < 0 && max != 0) {
			ret = Math.abs(ret) % max;
		}
		return ret;
	}

	protected long genPositiveLong(long min, long max) {
		long ret = genPositiveLong(max - min) + min;
		return ret;
	}

	protected double genDouble(double max) {
		double d = random.nextDouble();
		if (d > max && max != 0) {
			d = d % max;
		}
		return d;
	}

	protected double genDouble(double min, double max) {
		return genDouble(max - min) + min;
	}

	protected double genPositiveDouble(double max) {
		double ret = genDouble(max);
		if (ret < 0) {
			ret = Math.abs(ret) % max;
		}
		return ret;
	}

	protected double genPositiveDouble(double min, double max) {
		return genPositiveDouble(max - min) + min;
	}

	public String genString(int min, int max) {
		int len = genPositiveInt(min, max);
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < len; i++) {
			buf.append(ALPHA_NUMERIC[genPositiveInt(ALPHA_NUMERIC.length)
					% ALPHA_NUMERIC.length]);
		}
		return buf.toString();
	}

	public String genLetters(int min, int max) {
		int len = genPositiveInt(min, max);
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < len; i++) {
			buf.append(ALPHA[genPositiveInt(ALPHA.length) % ALPHA.length]);
		}
		return buf.toString();
	}

	public String genNumerics(int min, int max) {
		int len = genPositiveInt(min, max);
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < len; i++) {
			buf.append(NUMERIC[genPositiveInt(NUMERIC.length) % NUMERIC.length]);
		}
		return buf.toString();
	}

	public Date genDate(Date min, Date max) {
		long mi = (min == null ? 0 : min.getTime());
		long ma = (max == null ? System.currentTimeMillis() : max.getTime());
		long diff = ma - mi;
		long x = genPositiveLong(diff);
		Date t = new Date(mi + x);
		return t;
	}

	public Calendar genDate(Calendar min, Calendar max) {
		long mi = (min == null ? 0 : min.get(Calendar.MILLISECOND));
		long ma = (max == null ? System.currentTimeMillis() : max
				.get(Calendar.MILLISECOND));
		long diff = ma - mi;
		long x = genPositiveLong(diff);
		Date t = new Date(mi + x);
		Calendar ret = Calendar.getInstance();
		ret.setTime(t);
		return ret;
	}

	public String genDate(String min, String max, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date dmin = (min == null) ? null : parseDate(sdf, min);
		Date dmax = (max == null) ? null : parseDate(sdf, max);
		Date ret = genDate(dmin, dmax);
		return sdf.format(ret);
	}

	private Date parseDate(SimpleDateFormat sdf, String dstr) {
		try {
			return sdf.parse(dstr);
		} catch (ParseException e) {
			return null;
		}
	}
	
	//int(i, x)long(0, 50)

	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("Usage: SampleDataGenerator <field_spec> [delimiter]");
			System.exit(1);
		}
		String delimiter = null;
		if(args.length>=2){
			delimiter = args[1];
		}
		String fileSpec = args[0];
		
		
		
	}

}
