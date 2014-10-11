package com.asksunny.odbc;

public class PostgresSQLTranslator {

	public static String translateSQL(String sql, boolean escapeProcessing) {
		if (sql == null) {
			throw new SQLProxyException("SQL could not be null");
		}
		if (!escapeProcessing) {
			return sql;
		}
		if (sql.indexOf('{') < 0) {
			return sql;
		}
		int len = sql.length();
		char[] chars = null;
		int level = 0;
		for (int i = 0; i < len; i++) {
			char c = sql.charAt(i);
			switch (c) {
			case '\'':
			case '"':
			case '/':
			case '-':
				i = translateGetEnd(sql, i, c);
				break;
			case '{':
				level++;
				if (chars == null) {
					chars = sql.toCharArray();
				}
				chars[i] = ' ';
				while (Character.isSpaceChar(chars[i])) {
					i++;
					checkRunOver(i, len, sql);
				}
				int start = i;
				if (chars[i] >= '0' && chars[i] <= '9') {
					chars[i - 1] = '{';
					while (true) {
						checkRunOver(i, len, sql);
						c = chars[i];
						if (c == '}') {
							break;
						}
						switch (c) {
						case '\'':
						case '"':
						case '/':
						case '-':
							i = translateGetEnd(sql, i, c);
							break;
						default:
						}
						i++;
					}
					level--;
					break;
				} else if (chars[i] == '?') {
					i++;
					checkRunOver(i, len, sql);
					while (Character.isSpaceChar(chars[i])) {
						i++;
						checkRunOver(i, len, sql);
					}
					if (sql.charAt(i) != '=') {
						throw syntaxError(i, sql);
					}
					i++;
					checkRunOver(i, len, sql);
					while (Character.isSpaceChar(chars[i])) {
						i++;
						checkRunOver(i, len, sql);
					}
				}
				while (!Character.isSpaceChar(chars[i])) {
					i++;
					checkRunOver(i, len, sql);
				}
				int remove = 0;
				if (found(sql, start, "fn")) {
					remove = 2;
				} else if (found(sql, start, "escape")) {
					break;
				} else if (found(sql, start, "call")) {
					break;
				} else if (found(sql, start, "oj")) {
					remove = 2;
				} else if (found(sql, start, "ts")) {
					break;
				} else if (found(sql, start, "t")) {
					break;
				} else if (found(sql, start, "d")) {
					break;
				} else if (found(sql, start, "params")) {
					remove = "params".length();
				}
				for (i = start; remove > 0; i++, remove--) {
					chars[i] = ' ';
				}
				break;
			case '}':
				if (--level < 0) {
					throw syntaxError(i, sql);
				}
				chars[i] = ' ';
				break;
			case '$':
				i = translateGetEnd(sql, i, c);
				break;
			default:
			}
		}
		if (level != 0) {
			throw syntaxError(sql.length() - 1, sql);
		}
		if (chars != null) {
			sql = new String(chars);
		}
		return sql;
	}

	private static SQLProxyException syntaxError(int i, String sql) {
		throw new SQLProxyException(String.format(
				"Invalid SQL Syntax: at column %d, %s", i, sql));
	}

	private static void checkRunOver(int i, int len, String sql) {
		if (i >= len) {
			syntaxError(i, sql);
		}
	}

	private static boolean found(String sql, int start, String other) {
		return sql.regionMatches(true, start, other, 0, other.length());
	}

	private static int translateGetEnd(String sql, int i, char c) {
		int len = sql.length();
		switch (c) {
		case '$': {
			if (i < len - 1 && sql.charAt(i + 1) == '$'
					&& (i == 0 || sql.charAt(i - 1) <= ' ')) {
				int j = sql.indexOf("$$", i + 2);
				if (j < 0) {
					throw syntaxError(i, sql);
				}
				return j + 1;
			}
			return i;
		}
		case '\'': {
			int j = sql.indexOf('\'', i + 1);
			if (j < 0) {
				throw syntaxError(i, sql);
			}
			return j;
		}
		case '"': {
			int j = sql.indexOf('"', i + 1);
			if (j < 0) {
				throw syntaxError(i, sql);
			}
			return j;
		}
		case '/': {
			checkRunOver(i + 1, len, sql);
			if (sql.charAt(i + 1) == '*') {
				// block comment
				int j = sql.indexOf("*/", i + 2);
				if (j < 0) {
					throw syntaxError(i, sql);
				}
				i = j + 1;
			} else if (sql.charAt(i + 1) == '/') {
				// single line comment
				i += 2;
				while (i < len && (c = sql.charAt(i)) != '\r' && c != '\n') {
					i++;
				}
			}
			return i;
		}
		case '-': {
			checkRunOver(i + 1, len, sql);
			if (sql.charAt(i + 1) == '-') {
				// single line comment
				i += 2;
				while (i < len && (c = sql.charAt(i)) != '\r' && c != '\n') {
					i++;
				}
			}
			return i;
		}
		default:
			throw syntaxError(i, sql);
		}
	}
}
