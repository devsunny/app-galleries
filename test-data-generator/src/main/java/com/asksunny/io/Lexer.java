package com.asksunny.io;

import java.io.IOException;

public interface Lexer {

	LexerToken nextToken() throws IOException;
	void close() throws IOException;
}
