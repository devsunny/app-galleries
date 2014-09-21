package com.asksunny.hadoop.mr;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class SpringMapper extends SpringMapReduceBase 
implements Mapper<LongWritable, Text, Text, NullWritable>
{

	public SpringMapper() {
		super();
	}

	
	@Override
	public void map(LongWritable arg0, Text inData,
			OutputCollector<Text, NullWritable> out, Reporter reporter)
			throws IOException 
	{
		
		if(getDataProcessor().accept(inData)){
			Text ret = getDataProcessor().process(inData);			
			if(ret!=null){
				out.collect(ret, NullWritable.get());
			}
		}		
	}
}
