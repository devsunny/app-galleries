package com.asksunny.batch.graph.sql;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public final class JdbcTypeMap {

	private static final Map<String, Integer> NAME_TO_TYPE_MAP = new HashMap<>();
	private static final Map<Integer, String> TYPE_TO_NAME_MAP = new HashMap<>();

	static {
		try {
			Field[] fields = Types.class.getFields();
			for (int i = 0; i < fields.length; i++) {
				Field fd = fields[i];
				NAME_TO_TYPE_MAP.put(fd.getName(), fd.getInt(null));
				TYPE_TO_NAME_MAP.put(fd.getInt(null), fd.getName());
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to extract JDBC type Map");
		}
	}

	public static String getTypeName(int jdbcType) {
		return TYPE_TO_NAME_MAP.get(jdbcType);
	}

	public static int getType(String jdbcTypeName) {

		if (jdbcTypeName == null) {
			return Types.VARCHAR;
		}
		Integer type = NAME_TO_TYPE_MAP.get(jdbcTypeName.toUpperCase());
		if (type == null) {
			throw new RuntimeException(String.format("Unrecognized JDBC type name:%s", jdbcTypeName));
		}
		return NAME_TO_TYPE_MAP.get(jdbcTypeName);
	}

	private JdbcTypeMap() {

	}

}
