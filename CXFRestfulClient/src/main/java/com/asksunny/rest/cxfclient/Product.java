package com.asksunny.rest.cxfclient;

import java.util.Date;



public class Product {
	private String name;
	private String description;
	private String manfacture;
	private int quantity;
	private double msrp;
	private Date manfacturedDate;
	
	
	
	

	public Date getManfacturedDate() {
		return manfacturedDate;
	}

	public void setManfacturedDate(Date manfacturedDate) {
		this.manfacturedDate = manfacturedDate;		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;

	}

	public String getManfacture() {
		return manfacture;
	}

	public void setManfacture(String manfacture) {
		this.manfacture = manfacture;

	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;

	}

	public double getMsrp() {
		return msrp;
	}

	public void setMsrp(double msrp) {
		this.msrp = msrp;

	}

}
