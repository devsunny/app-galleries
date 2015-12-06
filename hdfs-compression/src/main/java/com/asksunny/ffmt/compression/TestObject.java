package com.asksunny.ffmt.compression;

public class TestObject {
	
	int age;
	double price;
	String location;
	String description;
	long timeinmilli;
	long dob;
	long doq;
	double value;
	String comment;
	
	
	public TestObject() {
		super();
	}



	public TestObject(int age, double price, String location, String description, long timeinmilli, long dob, long doq,
			double value, String comment) {
		super();
		this.age = age;
		this.price = price;
		this.location = location;
		this.description = description;
		this.timeinmilli = timeinmilli;
		this.dob = dob;
		this.doq = doq;
		this.value = value;
		this.comment = comment;
	}



	public int getAge() {
		return age;
	}



	public void setAge(int age) {
		this.age = age;
	}



	public double getPrice() {
		return price;
	}



	public void setPrice(double price) {
		this.price = price;
	}



	public String getLocation() {
		return location;
	}



	public void setLocation(String location) {
		this.location = location;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public long getTimeinmilli() {
		return timeinmilli;
	}



	public void setTimeinmilli(long timeinmilli) {
		this.timeinmilli = timeinmilli;
	}



	public long getDob() {
		return dob;
	}



	public void setDob(long dob) {
		this.dob = dob;
	}



	public long getDoq() {
		return doq;
	}



	public void setDoq(long doq) {
		this.doq = doq;
	}



	public double getValue() {
		return value;
	}



	public void setValue(double value) {
		this.value = value;
	}



	public String getComment() {
		return comment;
	}



	public void setComment(String comment) {
		this.comment = comment;
	}



	@Override
	public String toString() {
		return "TestObject [age=" + age + ", price=" + price + ", location=" + location + ", description=" + description
				+ ", timeinmilli=" + timeinmilli + ", dob=" + dob + ", doq=" + doq + ", value=" + value + ", comment="
				+ comment + "]";
	}
	
	
	
	
}
