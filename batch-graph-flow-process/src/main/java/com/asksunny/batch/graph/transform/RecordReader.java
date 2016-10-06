package com.asksunny.batch.graph.transform;

import com.asksunny.batch.graph.BatchFlowContext;

public interface RecordReader {

	void init(BatchFlowContext flowContext);

	boolean next() throws Exception;

	Object getNext() throws Exception;
	
	void close();
	
	
}
