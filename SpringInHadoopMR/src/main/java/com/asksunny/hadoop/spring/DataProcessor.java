package com.asksunny.hadoop.spring;

import org.apache.hadoop.io.Text;

public interface DataProcessor 
{
	boolean accept(Text textData);
	Text process(Text textDataIn);
	
}
