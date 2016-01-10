package com.asksunny.codegen;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;

import com.asksunny.io.LHReader;

public class LHReaderTest {

	@Test
	public void test() throws Exception{
		LHReader reader = new LHReader(3, new StringReader("SE"));
		assertEquals('S', reader.peek(0));
		assertEquals('E', reader.peek(1));
		assertEquals(-1, reader.peek(2));		
		reader.close();
	}
	
	
	@Test
	public void test2() throws Exception{
		LHReader reader = new LHReader(3, new StringReader("SEQUENCE,min=1001,max=100000.0,step=2,uiname='account id',varname=accountId"));
		int c = -1;
		int i = 0;
		while((c=reader.read())!=-1){
			i++;
			if(i==80){
				break;
			}
			System.out.println(String.format(">>>>>>> %c", c));
		}
		
	}
	
	

}
