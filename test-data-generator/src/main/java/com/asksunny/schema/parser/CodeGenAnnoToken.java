package com.asksunny.schema.parser;

import com.asksunny.codegen.CodeGenTokenKind;
import com.asksunny.io.LexerToken;

public class CodeGenAnnoToken extends LexerToken {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String image;
	int line;
	int column;
	CodeGenTokenKind kind = CodeGenTokenKind.OTHER;

	public CodeGenAnnoToken() {
		super();
	}

	public CodeGenAnnoToken(String image, int line, int column) {
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
		return "CodeGenAnnoToken [image=" + image + ", kind=" + kind + "]";
	}

	public CodeGenTokenKind getKind() {
		return kind;
	}

	public void setKind(CodeGenTokenKind kind) {
		this.kind = kind;
	}

}
