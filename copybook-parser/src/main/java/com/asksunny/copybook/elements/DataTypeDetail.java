package com.asksunny.copybook.elements;

public class DataTypeDetail implements CopyBookElement {

	private boolean signed = false;
	
	
	
	public DataTypeDetail() {
		// TODO Auto-generated constructor stub
	}

	private String image;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}
	
	
	

}
