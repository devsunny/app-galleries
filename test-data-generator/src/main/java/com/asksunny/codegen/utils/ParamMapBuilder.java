package com.asksunny.codegen.utils;

import java.util.HashMap;
import java.util.Map;

public class ParamMapBuilder {

	public static ParamMapBuilder newBuilder() {
		return new ParamMapBuilder();
	}

	public ParamMapBuilder addMapEntry(String key, String value) {
		params.put(key, value);
		return this;
	}

	public ParamMapBuilder addMapEntry(String[] pairs) {
		if (pairs.length >= 2) {
			for (int i = 0; i < pairs.length; i = i + 2) {
				String key = pairs[i];
				String value = (i + 1 < pairs.length) ? pairs[i + 1] : null;
				params.put(key, value);
			}
		}
		return this;
	}

	public Map<String, String> buildMap() {
		return params;
	}

	Map<String, String> params;

	public ParamMapBuilder() {
		params = new HashMap<>();
	}

}
