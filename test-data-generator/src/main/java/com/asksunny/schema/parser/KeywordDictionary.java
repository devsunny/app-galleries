package com.asksunny.schema.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KeywordDictionary extends HashMap<String, Keyword> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public KeywordDictionary() {
		this.put("CREATE", Keyword.CREATE);
		this.put("TABLE", Keyword.TABLE);
		this.put("NOT", Keyword.NOT);
		this.put("NULL", Keyword.NULL);
		this.put("PRIMARY", Keyword.PRIMARY);
		this.put("KEY", Keyword.KEY);
		this.put("VARCHAR", Keyword.VARCHAR);
		this.put("NUMBER", Keyword.NUMBER);
		this.put("INT", Keyword.INT);
		this.put("BIGINT", Keyword.BIGINT);
		this.put("INTEGER", Keyword.INTEGER);
		this.put("DOUBLE", Keyword.DOUBLE);
		this.put("VARCHAR2", Keyword.VARCHAR2);
		this.put("BINARY", Keyword.BINARY);
		this.put("LONG", Keyword.LONG);
		this.put("DATE", Keyword.DATE);
		this.put("TIME", Keyword.TIME);
		this.put("TIMESTAMP", Keyword.TIMESTAMP);
		this.put("ALTER", Keyword.ALTER);
		this.put("ADD", Keyword.ADD);
		this.put("CONSTRAINT", Keyword.CONSTRAINT);
		this.put("FOREIGN", Keyword.FOREIGN);
		this.put("REFERENCES", Keyword.REFERENCES);
		this.put("UNIQUE", Keyword.UNIQUE);
		this.put("INDEX", Keyword.INDEX);
		this.put("BYTE", Keyword.BYTE);
		this.put("NOPARALLELCREATE", Keyword.CREATE);
		this.put("NOPARALLEL", Keyword.NOPARALLEL);
		this.put("PARALLEL", Keyword.PARALLEL);
		this.put("*", Keyword.ASTERISK);
	}
	
	
	

	@Override
	public Keyword get(Object key) {
		
		return super.get(key.toString().toUpperCase());
	}

	@Override
	public Keyword put(String key, Keyword value) {
		return super.put(key.toUpperCase(), value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Keyword> m) {
		Set<? extends String> keys = m.keySet();
		for (String string : keys) {
			super.put(string.toUpperCase(), m.get(string));
		}
	}

	@Override
	public boolean containsKey(Object key) {
		
		return super.containsKey(key.toString().toUpperCase());
	}

}
