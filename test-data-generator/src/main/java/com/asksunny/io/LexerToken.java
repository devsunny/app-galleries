package com.asksunny.io;

import java.io.Serializable;

public class LexerToken implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String image;
	private int line;
	private int column;
	
	
	public LexerToken() {		
	}


	public LexerToken(String image, int line, int column) {
		super();
		this.image = image;
		this.line = line;
		this.column = column;
	}


	public String getImage() {
		return image;
	}


	public void setImage(String image) {
		this.image = image;
	}


	public int getLine() {
		return line;
	}


	public void setLine(int line) {
		this.line = line;
	}


	public int getColumn() {
		return column;
	}


	public void setColumn(int column) {
		this.column = column;
	}


	@Override
	public String toString() {
		return String.format("%d:%d [%s]", this.line, this.column, this.image);
	}

}
