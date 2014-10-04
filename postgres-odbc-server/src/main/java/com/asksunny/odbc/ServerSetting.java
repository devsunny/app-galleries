package com.asksunny.odbc;

public class ServerSetting {

	private String name;
	private String setting;
	private String description;
	
	
		
	public ServerSetting(String name) {
		this(name,  null);
	}
	
	public ServerSetting(String name, String setting) {
		this(name,  setting, null);
	}
	
	public ServerSetting(String name, String setting, String description) {
		super();
		this.name = name;
		this.setting = setting;
		this.description = description;
	}


	public ServerSetting() {		
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getSetting() {
		return setting;
	}


	public void setSetting(String setting) {
		this.setting = setting;
		
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
		
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerSetting other = (ServerSetting) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	

}
