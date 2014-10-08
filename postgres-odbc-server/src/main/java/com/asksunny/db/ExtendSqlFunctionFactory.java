package com.asksunny.db;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class ExtendSqlFunctionFactory {

	private static final ConcurrentHashMap<String, Class<? extends ExtendSqlFunction>> functionMaps = new ConcurrentHashMap<String, Class<? extends ExtendSqlFunction>>();
	static{
		//register built-in function here;
	}
	public static void register(ExtendSqlFunction funct) throws SQLException {
		if (funct.getName() == null) {
			throw new SQLException(String.format(
					"Extend SQL Function does not have proper name:%s", funct
							.getClass().getName()),
					ExtendSqlFunction.EXTEND_SQL_FUNCTION_SQLSTATE, 9988);
		}
		functionMaps.put(funct.getName().toUpperCase(), funct.getClass());
	}

	public static void register(String functionName,
			Class<ExtendSqlFunction> clazz) throws SQLException {
		functionMaps.put(functionName.toUpperCase(), clazz);
	}

	public static void register(Class<ExtendSqlFunction> clazz)
			throws SQLException {
		String functionName = null;
		ExtendSqlFunction funct = null;
		try {
			funct = clazz.newInstance();
			functionName = funct.getName();
		} catch (Throwable t) {
			throw new SQLException(String.format(
					"failed to create Extend SQL Function:%s", functionName),
					ExtendSqlFunction.EXTEND_SQL_FUNCTION_SQLSTATE, t);
		}
		if (functionName == null) {
			throw new SQLException(String.format(
					"Extend SQL Function does not have proper name:%s",
					functionName),
					ExtendSqlFunction.EXTEND_SQL_FUNCTION_SQLSTATE, 9988);
		}
		functionMaps.put(functionName.toUpperCase(), clazz);
	}

	public static boolean hasSqlFunction(String functionName)
			throws SQLException {
		return functionMaps.containsKey(functionName.toUpperCase());
	}

	public static ExtendSqlFunction createExtendSqlFunction(String functionName)
			throws SQLException {
		if (!hasSqlFunction(functionName))
			throw new SQLException(String.format(
					"Undefined Extend SQL Function:%s", functionName),
					ExtendSqlFunction.EXTEND_SQL_FUNCTION_SQLSTATE, 99999);

		Class<? extends ExtendSqlFunction> clazz = functionMaps
				.get(functionName.toUpperCase());
		ExtendSqlFunction funct = null;
		try {
			funct = clazz.newInstance();
		} catch (Throwable t) {
			throw new SQLException(String.format(
					"failed to create Extend SQL Function:%s", functionName),
					ExtendSqlFunction.EXTEND_SQL_FUNCTION_SQLSTATE, t);
		}
		return funct;
	}

	private ExtendSqlFunctionFactory() {
		// TODO Auto-generated constructor stub
	}

}
