package com.asksunny.ebcdic;

public class Field extends Entity {

	public static enum NumericType {BINARY, COMP, COMP1, COMP2, COMP3, NONE}
	
	private int length = 0;
	private int decimalLength = 0;
	private boolean implicitDecimal = false;
	private boolean singed = false;
	private NumericType numericType = NumericType.NONE;
	
	
	public Field() {
		super();
	}


	public int getLength() {
		return length;
	}


	public void setLength(int length) {
		this.length = length;
	}


	public int getDecimalLength() {
		return decimalLength;
	}


	public void setDecimalLength(int decimalLength) {
		this.decimalLength = decimalLength;
	}


	public boolean isImplicitDecimal() {
		return implicitDecimal;
	}


	public void setImplicitDecimal(boolean implicitDecimal) {
		this.implicitDecimal = implicitDecimal;
	}


	public boolean isSinged() {
		return singed;
	}


	public void setSinged(boolean singed) {
		this.singed = singed;
	}

	
	

	@Override
	public String toString() {
		return "Field [getName()=" + getName() + ", getType()=" + getType()
				+ ", length=" + length + ", decimalLength=" + decimalLength
				+ ", implicitDecimal=" + implicitDecimal + ", singed=" + singed
				+ "]";
	}


	public NumericType getNumericType() {
		return numericType;
	}


	public void setNumericType(NumericType numericType) {
		this.numericType = numericType;
	}
	
	

}
