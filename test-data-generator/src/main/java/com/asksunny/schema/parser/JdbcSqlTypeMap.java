package com.asksunny.schema.parser;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public final class JdbcSqlTypeMap {
	private static final JdbcSqlTypeMap instance = new JdbcSqlTypeMap();
	private Map<String, Integer> jdbcTypeMap = new HashMap<>();

	private JdbcSqlTypeMap() {
		try {
			Class<Types> typesClzz = Types.class;
			Field[] fields = typesClzz.getFields();
			for (Field field : fields) {
				jdbcTypeMap.put(String.format("%s", field.getName()), field.getInt(null));
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to extact type info from JDBC types");
		}
	}

	public Integer findJdbcType(String name) {
		String lname = name;
		if (name.equalsIgnoreCase("VARCHAR2")) {
			lname = "VARCHAR";
		} else if (name.equalsIgnoreCase("NUMBER")) {
			lname = "NUMERIC";
		}else if (name.equalsIgnoreCase("INT")) {
			lname = "INTEGER";
		}else if (name.equalsIgnoreCase("LONG")) {
			lname = "BIGINT";
		}
		Integer t = jdbcTypeMap.get(lname.toUpperCase());
		if (t == null) {
			t = Types.OTHER;
		}
		return t;
	}

	public static JdbcSqlTypeMap getInstance() {
		return instance;
	}

}
