package com.asksunny.hadoop.spring;

import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyDataProcessor implements DataProcessor {

	protected static Logger logger  =LoggerFactory.getLogger(DummyDataProcessor.class);
	
	public DummyDataProcessor() {		
	}

	@Override
	public boolean accept(Text textData) {	
		return true;
	}

	@Override
	public Text process(Text textDataIn) {		
		if(logger.isDebugEnabled()) logger.debug(textDataIn.toString());		
		return new Text("Processed|" + textDataIn.toString());
	}

	

}
