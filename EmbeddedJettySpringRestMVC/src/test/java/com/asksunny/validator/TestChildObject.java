package com.asksunny.validator;

import java.util.List;

import com.asksunny.validator.annotation.FieldValidate;
import com.asksunny.validator.annotation.ValidationOperator;

public class TestChildObject {

	
	@FieldValidate(value="123456", operator=ValidationOperator.EQUALS)
	private int id;
	
	@FieldValidate(value={"NY", "NJ", "CT"}, operator=ValidationOperator.WITHIN, notNull=true)
	private String state;
	
	@FieldValidate(value="^S\\d{2}.+$", operator=ValidationOperator.REGEX_MATCH, notNull=false)
	private String someText;
	
	
	@FieldValidate(operator=ValidationOperator.BETWEEN, minValue="100", maxValue="1000")
	private long betweenTest;
	
	@FieldValidate(operator=ValidationOperator.GREATER, minValue="10", failedMessage="size must be bigger than 10")
	private long sizeInLog;
	
	@FieldValidate(operator=ValidationOperator.GREATER_OR_EQUALS, minValue="18")
	private int age;
	
	@FieldValidate(operator=ValidationOperator.LESS, maxValue="21", failedMessage="Must not older than 20")
	private int k12Age;
	
	@FieldValidate(operator=ValidationOperator.LESS_OR_EQUALS, maxValue="12")
	private int k5Age;
	
	
	@FieldValidate(notNull=true, minSize=1, maxSize=5 , operator=ValidationOperator.BETWEEN)
	private List<String> hobbies;
	
	public TestChildObject() 
	{
		
	}

	
	
	
	
	public TestChildObject(String state, String someText, long sizeInLog, int age, List<String> hobbies) {
		super();
		this.state = state;
		this.someText = someText;
		this.sizeInLog = sizeInLog;
		this.age = age;
		this.hobbies = hobbies;
	}





	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSomeText() {
		return someText;
	}

	public void setSomeText(String someText) {
		this.someText = someText;
	}

	public long getSizeInLog() {
		return sizeInLog;
	}

	public void setSizeInLog(long sizeInLog) {
		this.sizeInLog = sizeInLog;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public List<String> getHobbies() {
		return hobbies;
	}

	public void setHobbies(List<String> hobbies) {
		this.hobbies = hobbies;
	}





	public int getId() {
		return id;
	}





	public void setId(int id) {
		this.id = id;
	}





	public long getBetweenTest() {
		return betweenTest;
	}





	public void setBetweenTest(long betweenTest) {
		this.betweenTest = betweenTest;
	}





	public int getK12Age() {
		return k12Age;
	}





	public void setK12Age(int k12Age) {
		this.k12Age = k12Age;
	}





	public int getK5Age() {
		return k5Age;
	}





	public void setK5Age(int k5Age) {
		this.k5Age = k5Age;
	}
	
	

}
