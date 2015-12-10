package com.asksunny.schema.parser;

public class Token 
{
	String image;
	int line;
	int column;
	LexerTokenKind kind = LexerTokenKind.OTHER;
	private Keyword keyword = null;
	
	public Token() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Token(String image, int line, int column) {
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
	public LexerTokenKind getKind() {
		return kind;
	}
	public void setKind(LexerTokenKind kind) {
		this.kind = kind;
	}
	public Keyword getKeyword() {
		return keyword;
	}
	public void setKeyword(Keyword keyword) {
		this.keyword = keyword;
	}
	@Override
	public String toString() {
		return image;
	}
	
	
	
	
}
