package com.asksunny.schema.generator;

public class Address 
{

	String city;
	String state;
	String zip;
	int houseNumber;
	String street;
	
	
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
	public int getHouseNumber() {
		return houseNumber;
	}
	public void setHouseNumber(int houseNumber) {
		this.houseNumber = houseNumber;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public Address(String city, String state, String zip, int houseNumber, String street) {
		super();
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.houseNumber = houseNumber;
		this.street = street;
	}
	public Address() {
		super();		
	}
	
	public static Address newAddress(String city, String state, String zip, int houseNumber, String street)
	{
		return new Address(city, state, zip, houseNumber, street);
	}
	@Override
	public String toString() {
		return  houseNumber + " " + street + ", " + city + ", " + state
				+ " " + zip;
	}
	
	
}
