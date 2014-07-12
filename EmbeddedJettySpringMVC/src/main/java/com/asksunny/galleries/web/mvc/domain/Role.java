package com.asksunny.galleries.web.mvc.domain;

import java.io.Serializable;

public class Role implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private int roleid;
	private String roleName;
	private String description;
	
	
	
	
	
	public int getRoleid() {
		return roleid;
	}





	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}





	public String getRoleName() {
		return roleName;
	}





	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}





	public String getDescription() {
		return description;
	}





	public void setDescription(String description) {
		this.description = description;
	}





	public Role() {
		
	}

}
