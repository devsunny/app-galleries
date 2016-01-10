package com.asksunny.io;

import java.io.IOException;

public class LHTokenReader implements Lexer {

	private Lexer lexer;
	private LexerToken[] buffer = null;
	private int rindex = 0;
	private int windex = 0;
	private int bufferLength = 0;	

	public LHTokenReader(int lookaheadSize, Lexer lexer) throws IOException{
		buffer = new LexerToken[lookaheadSize];
		this.bufferLength = lookaheadSize;
		this.lexer = lexer;
		fillBuffer(this.bufferLength);
	}
	
	
	protected void fillBuffer(int num) throws IOException {

		for (int i = 0; i < num; i++) {
			int widx = windex % bufferLength;
			buffer[widx] = lexer.nextToken();			
			if (buffer[widx] == null) {				
				break;
			} else {
				windex++;
			}
		}
	}
	
	
	public LexerToken peek(int idx) {		
		int rdx = rindex + idx;
		if (rdx > windex) {
			throw new IndexOutOfBoundsException("End of text stream.");
		}
		return buffer[rdx % bufferLength];
	}
	
	
	public LexerToken nextToken() throws IOException
	{
		LexerToken ret = null;
		if(rindex==windex){
			return ret;
		}
		ret = buffer[rindex % bufferLength];
		rindex++;
		fillBuffer(1);
		return ret;		
	}
	

	
	public void close() throws IOException {
		lexer.close();
	}
	

}
