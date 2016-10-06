package com.asksunny.batch.graph.transform;

import com.asksunny.batch.graph.BatchFlowContext;

public interface RecordTransformer {
	void init(BatchFlowContext flowContext);

	Object transform(Object transformee) throws Exception;

	/**
	 * No more records in the pipeline, needs to shutdown transformer to clean
	 * up resources
	 * 
	 * @throws Exception
	 */
	void shutdown() throws Exception;
}
