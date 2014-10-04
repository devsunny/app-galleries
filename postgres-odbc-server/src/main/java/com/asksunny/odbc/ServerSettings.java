package com.asksunny.odbc;

import java.util.HashMap;

public class ServerSettings extends HashMap<String, ServerSetting> {

	public ServerSetting findSetting(String name) {
		return this.get(name);
	}
	
	
	public String getSetting(String name) {
		return this.get(name)==null?null:this.get(name).getSetting();
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServerSettings() {
		super(13);
	}

	public ServerSettings(int initialCapacity) {
		super(initialCapacity);
	}

	public ServerSettings(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

}
