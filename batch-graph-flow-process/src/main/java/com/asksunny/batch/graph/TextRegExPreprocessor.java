package com.asksunny.batch.graph;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextRegExPreprocessor implements TextPreprocessor {

	public static final Pattern SUB_VARIABLE = Pattern.compile("@\\{(\\w+)\\}");
	public static final Pattern INTEGER = Pattern.compile("^(\\d+)$");

	public TextRegExPreprocessor() {
	}

	@Override
	public String preprocess(String text, Object parameter) {
		if (parameter == null) {
			return text;
		} else if (parameter instanceof Map) {

		} else if (parameter instanceof Properties) {

		} else if (parameter instanceof List) {

		} else {

		}

		return null;
	}

	protected String preprocess(String text, Map<String, Object> parameters) {

		Matcher matcher = SUB_VARIABLE.matcher(text);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			String varName = matcher.group(1);
			Object param = parameters.get(varName);
			if (!parameters.containsKey(varName)) {
				throw new RuntimeException(String.format("Undefine SQL statement subtitute varaible:%s", varName));
			} else if (param != null) {
				matcher.appendReplacement(buffer, param.toString());
			} else {
				matcher.appendReplacement(buffer, "");
			}
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	protected String preprocess(String text, Properties parameters) {

		Matcher matcher = SUB_VARIABLE.matcher(text);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			String varName = matcher.group(1);
			String param = parameters.getProperty(varName);
			if (!parameters.containsKey(varName)) {
				throw new RuntimeException(String.format("Undefine SQL statement subtitute varaible:%s", varName));
			} else if (param != null) {
				matcher.appendReplacement(buffer, param);
			} else {
				matcher.appendReplacement(buffer, "");
			}
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	protected String preprocess(String text, @SuppressWarnings("rawtypes") List parameters) {

		Matcher matcher = SUB_VARIABLE.matcher(text);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			String varName = matcher.group(1);
			Object param = null;
			if (INTEGER.matcher(varName).find()) {
				int idx = Integer.valueOf(varName);
				if (idx >= parameters.size() || idx < 0) {
					throw new RuntimeException(
							String.format("Invalid SQL statement subtitute varaible index:%s", varName));
				} else {
					param = parameters.get(idx);
				}
			}
			if (param != null) {
				matcher.appendReplacement(buffer, param.toString());
			} else {
				matcher.appendReplacement(buffer, "");
			}
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	protected String preprocess(String text, String parameter) {

		Matcher matcher = SUB_VARIABLE.matcher(text);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(buffer, parameter);
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
}
