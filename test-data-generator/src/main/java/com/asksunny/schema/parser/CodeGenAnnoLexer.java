package com.asksunny.schema.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import com.asksunny.codegen.CodeGenTokenKind;
import com.asksunny.io.Lexer;
import com.asksunny.parser.LookaheadReader;

public class CodeGenAnnoLexer implements Lexer
{

	private LookaheadReader lhReader = null;
	private int line;
	private int column;

	public CodeGenAnnoLexer(Reader reader, int line, int column) throws IOException {
		lhReader = new LookaheadReader(3, reader);
		this.line = line;
		this.column = column;
	}

	public CodeGenAnnoLexer(InputStream in, int line, int column) throws IOException {
		lhReader = new LookaheadReader(3, new InputStreamReader(in, Charset.defaultCharset()));
		this.line = line;
		this.column = column;
	}

	public CodeGenAnnoToken nextToken() throws IOException {
		CodeGenAnnoToken ret = null;
		StringBuilder buf = new StringBuilder();
		int c = -1;
		while ((c = lhReader.read()) != -1) {
			this.column++;
			switch (c) {
			case '\r':
			case '\n':
				return null;
			case ' ':
			case '\t':
				break;
			case '"':
				readTo('"', buf, false);
				ret = new CodeGenAnnoToken(buf.toString(), line, column);
				ret.setKind(CodeGenTokenKind.STRING_LITERAL);
				return ret;
			case '\'':
				readTo('\'', buf, false);
				ret = new CodeGenAnnoToken(buf.toString(), line, column);
				ret.setKind(CodeGenTokenKind.STRING_LITERAL);
				return ret;
			case '=':
				ret = new CodeGenAnnoToken("=", line, column);
				ret.setKind(CodeGenTokenKind.ASSIGNMENT);
				return ret;
			case ',':
				ret = new CodeGenAnnoToken(",", line, column);
				ret.setKind(CodeGenTokenKind.COMMA);
				return ret;
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'G':
			case 'H':
			case 'I':
			case 'J':
			case 'K':
			case 'L':
			case 'M':
			case 'N':
			case 'O':
			case 'P':
			case 'Q':
			case 'R':
			case 'S':
			case 'T':
			case 'U':
			case 'V':
			case 'W':
			case 'X':
			case 'Y':
			case 'Z':
				buf.append((char) c);
				readIdentifier(buf);
				ret = new CodeGenAnnoToken(buf.toString(), line, column);
				ret.setKind(CodeGenTokenKind.IDENTIFIER);
				return ret;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				buf.append((char) c);
				readNumber(buf);
				ret = new CodeGenAnnoToken(buf.toString(), line, column);
				ret.setKind(CodeGenTokenKind.NUMBER_LITERAL);
				return ret;
			default:
				break;
			}
			
		}

		return ret;
	}

	protected void readNumber(StringBuilder buf) throws IOException {

		do {
			int ic = lhReader.peek(0);
			if (ic == -1 || ic == '=' || ic == ' ' || ic == '\t' || (ic != '.' && (ic > '9' || ic < '0'))) {
				break;
			}
			buf.append((char) readChar());
		} while (true);
	}

	protected void readIdentifier(StringBuilder buf) throws IOException {

		do {
			int ic = lhReader.peek(0);
			if (ic == -1 || ic == '='  || ic == ',' || ic == ' ' || ic == '\t') {
				break;
			}
			buf.append((char) readChar());
		} while (true);
	}

	protected int readChar() throws IOException {
		this.column++;
		return lhReader.read();
	}

	protected void readTo(int c, StringBuilder buf, boolean inclusive) throws IOException {
		do {
			int ic = lhReader.peek(0);
			if (ic == -1) {
				break;
			}
			if (ic == c) {
				if (inclusive) {
					buf.append((char) readChar());
				} else {
					readChar();
				}
				break;
			} else {
				buf.append((char) readChar());
			}
		} while (true);

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

	public static void main(String[] ags) {
		for (int i = 'a'; i <= 'z'; i++) {
			System.out.println(String.format("case '%c':", i));
		}
		for (int i = '0'; i <= '9'; i++) {
			System.out.println(String.format("case '%c':", i));
		}
	}

	@Override
	public void close() throws IOException {
		this.lhReader.close();		
	}

}
