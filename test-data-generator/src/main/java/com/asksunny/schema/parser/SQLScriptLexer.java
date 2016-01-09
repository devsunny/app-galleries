package com.asksunny.schema.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import com.asksunny.parser.LookaheadReader;

public class SQLScriptLexer {

	private LookaheadReader lhReader = null;
	private int line = 1;
	private int column = 1;
	private boolean eof = false;
	private KeywordDictionary kdict = new KeywordDictionary();

	public SQLScriptLexer(Reader reader) throws IOException {
		lhReader = new LookaheadReader(3, reader);
	}

	public SQLScriptLexer(InputStream in) throws IOException {
		lhReader = new LookaheadReader(3, new InputStreamReader(in, Charset.defaultCharset()));
	}

	
	public Token nextToken() throws IOException {
		Token ret = null;
		if (eof) {
			return ret;
		}
		StringBuilder buf = new StringBuilder();
		while (ret == null) {
			int c = lhReader.read();
			//System.err.println(String.format("%d:%d [%c]", line, column,  (char)c));
			column++;
			switch (c) {
			case -1:
				return null;
			case ' ':
			case '\t':
			case '\r':
				break;
			case '\n':
				line++;
				column = 1;
				break;
			case ')':
				buf.append((char) c);
				ret = new Token(buf.toString(), line, column);
				buf.setLength(0);
				ret.setKind(LexerTokenKind.RPAREN);
				break;
			case '(':
				buf.append((char) c);
				ret = new Token(buf.toString(), line, column);
				buf.setLength(0);
				ret.setKind(LexerTokenKind.LPAREN);
				break;
			case ',':
				buf.append((char) c);
				ret = new Token(buf.toString(), line, column);
				buf.setLength(0);
				ret.setKind(LexerTokenKind.COMMA);
				break;
			case '=':
				buf.append((char) c);
				ret = new Token(buf.toString(), line, column);
				buf.setLength(0);
				ret.setKind(LexerTokenKind.EQUAL);
				break;
			case '-':
				int l1 = lhReader.peek(0);
				int l2 = lhReader.peek(1);
				if (l1 == '-' && l2 == '#') {
					// annoatation comment;
					readChar();
					readChar();
					readToEnd('\n', buf);
					ret = new Token(buf.toString(), line, column);
					buf.setLength(0);
					ret.setKind(LexerTokenKind.ANNOTATION_COMMENT);
				} else if (l1 == '-') {
					// plain comment;
					readChar();
					readToEnd('\n', buf);
					ret = new Token(buf.toString(), line, column);
					buf.setLength(0);
					ret.setKind(LexerTokenKind.COMMENT);
					ret = null;
				} else {
					buf.append((char) c);
					ret = new Token(buf.toString(), line, column);
					buf.setLength(0);
				}
				break;
			case ';':
				buf.append((char) c);
				ret = new Token(buf.toString(), line, column);
				buf.setLength(0);
				ret.setKind(LexerTokenKind.SEMICOLON);
				break;
			case '"':
				readTo(c, buf, false);
				ret = new Token(buf.toString(), line, column);
				buf.setLength(0);
				ret.setKind(LexerTokenKind.DOUBLE_QUOTED_TEXT);
				break;
			case '\'':
				readTo(c, buf, false);
				ret = new Token(buf.toString(), line, column);
				buf.setLength(0);
				ret.setKind(LexerTokenKind.SINGLE_QUOTED_TEXT);
				break;
			default:
				buf.append((char) c);
				if (Character.isJavaIdentifierStart((char) c)) {
					readIdentifier(buf);
					ret = new Token(buf.toString(), line, column);					
					Keyword kw = kdict.get(ret.getImage());
					if (kw != null) {
						ret.setKind(LexerTokenKind.KEYWORD);
						ret.setKeyword(kw);
					} else {
						ret.setKind(LexerTokenKind.IDENTIFIER);
					}
				} else if (Character.isDigit((char) c)) {
					readNumber(buf);
					ret = new Token(buf.toString(), line, column);
					ret.setKind(LexerTokenKind.NUMBER);
				} else {
					ret = new Token(buf.toString(), line, column);
				}
				buf.setLength(0);
				break;
			}
		}

		return ret;
	}

	protected int readChar() throws IOException {
		this.column++;
		return lhReader.read();
	}

	protected void readNumber(StringBuilder buf) throws IOException {
		do {
			int ic = lhReader.peek(0);
			if (ic == -1) {
				eof = true;
				break;
			}
			if (Character.isDigit((char) ic) || ic == '.') {
				buf.append((char)readChar());
			} else {
				break;
			}
		} while (true);

	}

	protected void readTo(int c, StringBuilder buf, boolean inclusive) throws IOException {
		do {
			int ic = lhReader.peek(0);
			if (ic == -1) {
				eof = true;
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

	protected void readToEnd(int c, StringBuilder buf) throws IOException {
		do {
			int ic = lhReader.peek(0);
			if (ic == -1) {
				eof = true;
				break;
			}
			if (ic == c) {
				if (buf.charAt(buf.length() - 1) == '\r') {
					buf.deleteCharAt(buf.length() - 1);
				}
				break;
			} else {
				buf.append((char) readChar());
			}
		} while (true);

	}

	protected void readIdentifier(StringBuilder buf) throws IOException {
		do {
			int ic = lhReader.peek(0);
			if (ic == -1) {
				eof = true;
				break;
			}
			if (Character.isJavaIdentifierPart((char) ic)) {
				buf.append((char)readChar());
			} else {
				break;
			}
		} while (true);

	}
	
	public void close() throws IOException
	{
		this.lhReader.close();
	}

}
