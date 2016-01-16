package com.asksunny.codegen.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateUtil {
	private static final Pattern SYS_PROP_PATTERN = Pattern
			.compile("#\\{([^\\{\\}]+)\\}");
	private static final String EMPTY = "";
		
	public static String renderTemplate(CharSequence text,
			Map<String, String> props) {
		Matcher matcher = SYS_PROP_PATTERN.matcher(text);
		StringBuffer buf = new StringBuffer();
		while (matcher.find()) {
			String key = matcher.group(1);
			String value = props.get(key);
			if (value != null) {
				matcher.appendReplacement(buf, value);
			}else{
				matcher.appendReplacement(buf, EMPTY);
			}
		}
		matcher.appendTail(buf);
		return buf.toString();
	}
	
	private TemplateUtil() {		
	}

}
