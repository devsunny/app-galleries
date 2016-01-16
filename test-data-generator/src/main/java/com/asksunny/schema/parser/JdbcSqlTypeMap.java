package com.asksunny.schema.parser;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public final class JdbcSqlTypeMap {
	private static final JdbcSqlTypeMap instance = new JdbcSqlTypeMap();
	private Map<String, Integer> jdbcTypeMap = new HashMap<>();

	private Map<Integer, String> jdbcTypeInvertMap = new HashMap<>();

	private JdbcSqlTypeMap() {
		try {
			Class<Types> typesClzz = Types.class;
			Field[] fields = typesClzz.getFields();
			for (Field field : fields) {
				jdbcTypeMap.put(String.format("%s", field.getName()), field.getInt(null));
				jdbcTypeInvertMap.put(field.getInt(null), String.format("%s", field.getName()));
			}
			jdbcTypeMap.put("DATETIME", Types.TIMESTAMP);			
		} catch (Exception e) {
			throw new RuntimeException("Failed to extact type info from JDBC types");
		}
	}

	public String findJdbcTypeName(int jt) {
		return jdbcTypeInvertMap.get(jt);
	}

	public Integer findJdbcType(String name) {
		String lname = name;
		if (name.equalsIgnoreCase("VARCHAR2")) {
			lname = "VARCHAR";
		} else if (name.equalsIgnoreCase("NUMBER")) {
			lname = "NUMERIC";
		} else if (name.equalsIgnoreCase("INT")) {
			lname = "INTEGER";
		} else if (name.equalsIgnoreCase("LONG")) {
			lname = "BIGINT";
		}
		Integer t = jdbcTypeMap.get(lname.toUpperCase());
		if (t == null) {
			t = Types.OTHER;
		}
		return t;
	}

	public static Integer getJdbcTyep(String name) {
		return getInstance().findJdbcType(name);
	}

	public static String getJdbcTyepName(int jt) {
		return getInstance().findJdbcTypeName(jt);
	}
	
	public static String toJavaTypeName(com.asksunny.schema.Field field)
	{
		String ret = "String";
		switch (field.getJdbcType()) {
		case Types.BIT:
			ret = "Boolean";
			break;
		case Types.TINYINT:
		case Types.SMALLINT:
			ret = "Integer";
			break;
		case Types.INTEGER:
		case Types.BIGINT:
			ret = "Long";
			break;
		case Types.FLOAT:
		case Types.REAL:
		case Types.DOUBLE:		
		case Types.DECIMAL:
			ret = "Double";
			break;
		case Types.NUMERIC:
			if(field.getScale()==0){
				ret = "Long";
			}else{
				ret = "Double";
			}
			break;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			ret = "String";
			break;

		case Types.DATE:
			ret = "java.sql.Date";
			break;

		case Types.TIME:
			ret = "java.sql.Time";
			break;

		case Types.TIMESTAMP:
			ret = "java.sql.Timestamp";
			break;

		case Types.BINARY:
			ret = "byte[]";
			break;

		case Types.VARBINARY:
			ret = "byte[]";
			break;

		case Types.LONGVARBINARY:
			ret = "byte[]";
			break;

		case Types.BLOB:
			ret = "byte[]";
			break;

		case Types.CLOB:
			ret = "String";
			break;
		case Types.BOOLEAN:
			ret = "Boolean";
			break;

		case Types.ROWID:
			ret = "String";
			break;

		case Types.NCHAR:
			ret = "String";
			break;

		case Types.NVARCHAR:
			ret = "String";
			break;

		case Types.LONGNVARCHAR:
			ret = "String";
			break;

		case Types.NCLOB:
			ret = "String";
			break;

		case Types.SQLXML:
			ret = "String";
			break;
		// case Types.TIME_WITH_TIMEZONE:
		// ret = "java.sql.Time";
		// break;
		//
		// case Types.TIMESTAMP_WITH_TIMEZONE:
		// ret = "java.sql.Timestamp";
		// break;
		}
		return ret;
	}

	

	public static JdbcSqlTypeMap getInstance() {
		return instance;
	}

}
