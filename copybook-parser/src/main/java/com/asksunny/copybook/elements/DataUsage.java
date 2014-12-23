package com.asksunny.copybook.elements;

public class DataUsage implements CopyBookElement{

	public static int PIC9   = 0;
	public static int BINARY = 2;
	public static int COMP_0 = 2;
	public static int COMP_1 = 3;
	public static int COMP_2 = 4;
	public static int COMP_3 = 5;
	public static int COMP_4 = 6;
	public static int COMP_5 = 7;
	public static int COMP_6 = 8;
	public static int COMP_7 = 9;
	public static int COMP_8 = 10;
	
	private int usage;
	private String image;
	
	public DataUsage() {
		
	}

	public int getUsage() {
		return usage;
	}

	public void setUsage(int usage) {
		this.usage = usage;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	
	
	
	

}
