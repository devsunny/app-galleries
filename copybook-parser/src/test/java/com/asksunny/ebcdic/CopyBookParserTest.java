package com.asksunny.ebcdic;

import static org.junit.Assert.*;

import java.net.URLDecoder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CopyBookParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception{
		CopyBookParser parser = new CopyBookParser(URLDecoder.decode(getClass().getResource("/SimpleCopyBook.cb").getFile().trim(), "UTF-8"));
		parser.parse();
	}

}
