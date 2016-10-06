package com.asksunny.batch.graph.transform;

import java.util.List;

import com.asksunny.batch.graph.AbstractWorkflowTask;

public class WorkflowTransformTask extends AbstractWorkflowTask {

	private RecordReader recordReader;
	private RecordTransformer[] transformers;

	public WorkflowTransformTask() {
	}

	@Override
	protected void executeTask() throws Exception {
		for (int i = 0; i < transformers.length; i++) {
			transformers[i].init(getFlowContext());
		}
		while (recordReader.next()) {
			Object transformee = recordReader.getNext();
			for (int i = 0; i < transformers.length; i++) {
				transformee = transformers[i].transform(transformee);
				if (transformee == null) {
					break;
				}
			}
		}
		for (int i = 0; i < transformers.length; i++) {
			transformers[i].shutdown();
		}
	}

	public RecordReader getRecordReader() {
		return recordReader;
	}

	public void setRecordReader(RecordReader recordReader) {
		this.recordReader = recordReader;
	}

	public RecordTransformer[] getTransformers() {
		return transformers;
	}

	public void setTransformers(RecordTransformer[] transformers) {
		this.transformers = transformers;
	}

	public void setTransformers(List<RecordTransformer> transformers) {
		this.transformers = transformers.toArray(new RecordTransformer[transformers.size()]);
	}

}
