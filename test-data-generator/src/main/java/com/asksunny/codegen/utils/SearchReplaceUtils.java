package com.asksunny.codegen.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchReplaceUtils {
	private static final Pattern SYS_PROP_PATTERN = Pattern
			.compile("\\$\\{([^\\{\\}]+)\\}");
	
	
	public static String searchAndReplace(CharSequence text,
			Map<String, String> props) {
		Matcher matcher = SYS_PROP_PATTERN.matcher(text);
		StringBuffer buf = new StringBuffer();
		while (matcher.find()) {
			String key = matcher.group(1);
			String value = props.get(key);
			if (value != null) {
				if (SYS_PROP_PATTERN.matcher(value).find()) {
					value = searchAndReplace(value, props);
				}
				matcher.appendReplacement(buf, value);
			}
		}
		matcher.appendTail(buf);
		return buf.toString();
	}
	
	private SearchReplaceUtils() {		
	}

}
