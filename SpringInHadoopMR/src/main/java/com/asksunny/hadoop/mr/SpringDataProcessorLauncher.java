package com.asksunny.hadoop.mr;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

public class SpringDataProcessorLauncher {

	private Pattern INT_PATTERN = Pattern.compile("^\\d+$");
	
	
	public SpringDataProcessorLauncher() {	
		
	}
	
	protected static String usage()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("Usage: SpringDataProcessorLauncher [number_of_Mapper] <hdfs_path_to_spring_xml_context> <hdfs_path_to_input_files> <hdfs_path_to_output_dir>");
		return buf.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException
	{		
		if(args.length<3){
			throw new RuntimeException("Missing Required arguments:\n" + usage());
		}		
		int mappers = args.length>=4?Integer.valueOf(args[0]).intValue():2;
		Path outPath = args.length>=4?new Path(args[3]):new Path(args[2]);
		String inPaths = args.length>=4?(args[2]):(args[1]);
		String pathToSpringXml = args.length>=4?(args[1]):(args[0]);		
		JobConf conf = new JobConf(SpringDataProcessorLauncher.class);
		conf.setJobName("SpringDataProcessorLauncher");
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(NullWritable.class);
		conf.setMapperClass(SpringMapper.class);
		conf.setNumMapTasks(mappers);
		conf.setNumReduceTasks(0);
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		conf.set(SpringMapReduceBase.SPRING_XML_CONTEXT, pathToSpringXml);		
		FileOutputFormat.setOutputPath(conf, outPath);
		FileInputFormat.setInputPaths(conf, inPaths);
		JobClient.runJob(conf);

	}

}
