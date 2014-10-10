package com.asksunny.compress;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StreamCompressorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception{
		StreamCompressor compressor = new StreamCompressor(StreamCompressor.Mode.COMPRESS, StreamCompressor.Type.BZIP2, null);
		String testdata = "This is my test Datadsfsdgsfdhsvcxbvxcv";
		ByteArrayInputStream bin = new ByteArrayInputStream(testdata.getBytes());
		ByteArrayOutputStream bout = new ByteArrayOutputStream();		
		compressor.doCompressAction(bin, bout);	
		
		byte[] out = bout.toByteArray();
		System.out.println(new String(out));
		bin = new ByteArrayInputStream(out);
		bout = new ByteArrayOutputStream();		
		compressor.setMode(StreamCompressor.Mode.DECOMPRESS);
		compressor.doCompressAction(bin, bout);			
		System.out.println("------------------------");
		System.out.println(new String(bout.toByteArray()));
		System.out.println("------------------------");
	}

}
