package com.asksunny.hadoop.mr;

import java.io.Closeable;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobConfigurable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.hadoop.spring.DataProcessor;
import com.asksunny.hadoop.spring.HDFSXmlApplicationContext;

public abstract class SpringMapReduceBase implements Closeable, JobConfigurable{
	protected static Logger logger  =LoggerFactory.getLogger(SpringMapReduceBase.class);
	private HDFSXmlApplicationContext applicationContext = null;
	private JobConf jobConfig;
	public final static String SPRING_XML_CONTEXT = "spring.xml.context";
	private DataProcessor dataProcessor;
	
	
	@Override
	public void configure(JobConf jobConfig) 
	{		
		String springXmlPath = jobConfig.get(SPRING_XML_CONTEXT);
		logger.info("Spring configuration location:{}",springXmlPath);
		try{
			FileSystem hdfs = FileSystem.get(new Configuration ());		
			applicationContext = new HDFSXmlApplicationContext(hdfs, springXmlPath);			
			dataProcessor = applicationContext.getBean(DataProcessor.class);			
		}catch(Exception ex){
			logger.error("Failed to load Sprring", ex);
			throw new RuntimeException("Failed to load Sprring", ex);
		}		
	}
	
	

	public JobConf getJobConfig() {
		return jobConfig;
	}



	public void setJobConfig(JobConf jobConfig) {
		this.jobConfig = jobConfig;	
	}
	
	@Override
	public void close() throws IOException {
		if(applicationContext!=null){
			applicationContext.close();
		}		
	}

	public HDFSXmlApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(HDFSXmlApplicationContext applicationContext) {
		this.applicationContext = applicationContext;		
	}



	public DataProcessor getDataProcessor() {
		return dataProcessor;
	}



	public void setDataProcessor(DataProcessor dataProcessor) {
		this.dataProcessor = dataProcessor;		
	}
	
	

	
}
