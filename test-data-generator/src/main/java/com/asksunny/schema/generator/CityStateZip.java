package com.asksunny.schema.generator;

public class CityStateZip {
	String city;
	String state;
	String zip;
	String type;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CityStateZip() {
		super();
	}

	public CityStateZip(String city, String state, String zip, String type) {
		super();
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.type = type;
	}
	
	public static CityStateZip newCityStateZip(String city, String state, String zip, String type) 
	{
		return new CityStateZip(city, state, zip, type);
	}

}
