package com.asksunny.codegen.utils;

public class JavaIdentifierUtil {

	public static String toCamelCase(String text, boolean capFirstLetter) {
		String[] parts = text.split("[_]");
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			String lc = parts[i].toLowerCase();
			if (i > 0 || capFirstLetter) {
				buf.append(Character.toUpperCase(lc.charAt(0)));
				buf.append(lc.substring(1));
			} else {
				buf.append(lc);
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

	public static String decapitalize(String text) {
		if (text == null)
			return null;
		if (text.length() == 1) {
			return String.valueOf(Character.toLowerCase(text.charAt(0)));
		}
		return Character.toLowerCase(text.charAt(0)) + text.substring(1);
	}
	
	public static String capitalize(String text) {
		if (text == null)
			return null;
		if (text.length() == 1) {
			return String.valueOf(Character.toUpperCase(text.charAt(0)));
		}
		return Character.toUpperCase(text.charAt(0)) + text.substring(1);
	}

	private JavaIdentifierUtil() {
	}

}
