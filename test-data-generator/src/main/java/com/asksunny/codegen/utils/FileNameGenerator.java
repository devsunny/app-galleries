package com.asksunny.codegen.utils;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameGenerator {

	private static final Pattern SYS_PROP_PATTERN = Pattern.compile("#\\{([^\\{\\}]+)\\}");
	private static final SecureRandom random = new SecureRandom(UUID.randomUUID().toString().getBytes());
	private static final long DAY = 1000 * 60 * 60 * 24;
	private static final int DAY_INT = 1000 * 60 * 60 * 24;

	public static String genFileName(CharSequence text) {
		return genFileName(text, new HashMap<String, String>());
	}

	public static String genExt(String[] exts) {
		return exts[Math.abs(random.nextInt(exts.length)) % exts.length];
	}

	public static String genFileName(CharSequence text, Map<String, String> reUses) {
		Matcher matcher = SYS_PROP_PATTERN.matcher(text);
		StringBuffer buf = new StringBuffer();
		while (matcher.find()) {
			String key = matcher.group(1);
			String value = null;
			if (key.equalsIgnoreCase("DATE")) {
				value = reUses.get("DATE") == null ? getDate() : reUses.get("DATE");
				reUses.put("DATE", value);
			} else if (key.equalsIgnoreCase("TIME")) {
				value = reUses.get("TIME") == null ? getTime() : reUses.get("TIME");
				reUses.put("TIME", value);
			} else if (key.equalsIgnoreCase("TIMESTAMP")) {
				value = reUses.get("TIMESTAMP") == null ? getTimestamp() : reUses.get("TIMESTAMP");
				reUses.put("TIMESTAMP", value);
			} else if (key.matches("^[Nn]+$")) {
				long seqp = reUses.get("SEQ") == null ? 0 : Long.valueOf(reUses.get("SEQ"));
				value = getSequence(key, seqp);
				reUses.put("SEQ", Long.toString(seqp + 1));
			}
			if (value != null) {
				matcher.appendReplacement(buf, value);
			}
		}
		matcher.appendTail(buf);
		return buf.toString();
	}

	protected static String getDate() {
		long num = Math.abs(random.nextInt(730));
		long past = System.currentTimeMillis() - (num * DAY);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date(past));
	}

	protected static String getTime() {
		long num = Math.abs(random.nextInt(DAY_INT));
		long past = System.currentTimeMillis() - num;
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		return sdf.format(new Date(past));
	}

	protected static String getTimestamp() {
		long numd = Math.abs(random.nextInt(730));
		long num = Math.abs(random.nextInt(DAY_INT));
		long past = System.currentTimeMillis() - num - (numd * DAY);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return sdf.format(new Date(past));
	}

	protected static String getSequence(String nnn, long p) {
		String fmt = "%0" + nnn.length() + "d";
		return String.format(fmt, p + 1);
	}

	public static void main(String[] args) {
		System.out.println(genFileName("SOR1_en2_${DATE}_${NNNN}"));
	}

}
