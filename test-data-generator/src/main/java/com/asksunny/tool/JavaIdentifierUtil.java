package com.asksunny.tool;

public class JavaIdentifierUtil {

	public static String toCamelCase(String text, boolean capFirstLetter) {

		StringBuilder buf = new StringBuilder();
		if (text.indexOf("_") != -1) {
			String[] parts = text.split("[_]");
			for (int i = 0; i < parts.length; i++) {
				String lc = parts[i].toLowerCase();
				if (i > 0 || capFirstLetter) {
					buf.append(Character.toUpperCase(lc.charAt(0)));
					buf.append(lc.substring(1));
				} else {
					buf.append(lc);
				}
			}
		} else {
			if (capFirstLetter) {
				buf.append(Character.toUpperCase(text.charAt(0)));
				buf.append(text.substring(1));
			} else {
				buf.append(text);
			}
		}
		return buf.toString();
	}

	public static String toObjectName(String text) {
		return toCamelCase(text, true);
	}

	public static String toVariableName(String text) {
		return toCamelCase(text, false);
	}

	private JavaIdentifierUtil() {
	}

}
