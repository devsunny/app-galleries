package com.asksunny.schema;

import java.util.List;

public interface IEntityDataGenerator {
	public List<List<String>> generateDataSet();

	public void generateFullDataSet();

	public Entity getEntity();

	public void setEntity(Entity entity);

	public List<BottomUpEntityDataGenerator> getParentEntityGenerators();

	public void setParentEntityGenerators(List<BottomUpEntityDataGenerator> parentEntityGenerators);

	public void open();

	public void close();

	public long getTotalRecordCount();

	public void setTotalRecordCount(long totalRecordCount);

	public SchemaOutputType getOutputType();

	public void setOutputType(SchemaOutputType outputType);

	public String getOutputUri();

	public void setOutputUri(String outputUri);
}
