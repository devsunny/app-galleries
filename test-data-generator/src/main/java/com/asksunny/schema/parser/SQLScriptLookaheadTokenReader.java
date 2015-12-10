package com.asksunny.schema.parser;

import java.io.IOException;

public class SQLScriptLookaheadTokenReader {

	private int lhcount;
	private SQLScriptLexer sqlLexer;
	private final Token[] lookaheadBuffer;
	
	private int readPos = 0;

	public SQLScriptLookaheadTokenReader(int lhcount, SQLScriptLexer sqlLexer) throws IOException {
		super();
		this.lhcount = lhcount;
		this.sqlLexer = sqlLexer;
		this.lookaheadBuffer = new Token[lhcount];
		fillBuffer();
	}

	protected void fillBuffer() throws IOException {
		for (int i = 0; i < lhcount; i++) {
			lookaheadBuffer[i] = sqlLexer.nextToken();
			if (lookaheadBuffer[i] == null) {			
				return;
			}
		}
	}

	public Token peek(int idx) {
		if (idx >= lhcount) {
			throw new IndexOutOfBoundsException(
					String.format("Lookaheader buffer size %s lookup index %d", lhcount, idx));
		}
		int index = (idx + readPos) % lhcount;
		return lookaheadBuffer[index];
	}

	public Token read() throws IOException {
		int index = readPos % lhcount;
		if (lookaheadBuffer[index] == null) {
			return null;
		}
		Token token = lookaheadBuffer[index];
		lookaheadBuffer[index] = sqlLexer.nextToken();		
		this.readPos++;
		return token;
	}
	
	public void close() throws IOException
	{
		this.sqlLexer.close();
	}

}
