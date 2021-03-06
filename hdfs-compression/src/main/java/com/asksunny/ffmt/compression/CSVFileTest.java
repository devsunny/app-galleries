package com.asksunny.ffmt.compression;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.hadoop.io.compress.GzipCodec;

//44 GB.It tooks 851 seconds 
//6 GB.It tooks 1490 seconds GZIP
public class CSVFileTest extends BenchmarkBase {

	@Override
	protected void writeObjects(OutputStream fout) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(fout);
		PrintWriter out = new PrintWriter(writer);
		for (long i = 0; i < getNumberOfRecord(); i++) {
			out.print(inputBaseObject.age + i);
			out.print(",");
			out.print(inputBaseObject.price + i);
			out.print(",");
			out.print(inputBaseObject.location + i);
			out.print(",");
			out.print(inputBaseObject.description + i);
			out.print(",");
			out.print(inputBaseObject.timeinmilli + i);
			out.print(",");
			out.print(inputBaseObject.dob + i);
			out.print(",");
			out.print(inputBaseObject.doq + i);
			out.print(",");
			out.print(inputBaseObject.value + i);
			out.print(",");
			out.println(inputBaseObject.comment + i);
		}
		out.flush();
	}
	
	
	public static void main(String[] args) throws Exception 
	{
		CSVFileTest test = new CSVFileTest();		
		test.start();
		test.write("snappy", "target/csvTestObject.txt.snappy");
		long d1  = test.stopInSeconds();
		System.out.printf("It tooks %d seconds to generate %d records in %s format\n", d1, test.getNumberOfRecord(), "snappy");
		
		
		
	}
	

}
