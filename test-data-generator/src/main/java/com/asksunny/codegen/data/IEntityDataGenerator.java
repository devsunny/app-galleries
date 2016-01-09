package com.asksunny.codegen.data;

import java.util.List;

import com.asksunny.schema.Entity;

public interface IEntityDataGenerator {
	public List<List<String>> generateDataSet();

	public void generateFullDataSet();

	public Entity getEntity();

	public void setEntity(Entity entity);

	public List<BottomUpEntityDataGenerator> getParentEntityGenerators();

	public void setParentEntityGenerators(List<BottomUpEntityDataGenerator> parentEntityGenerators);

	public void open();

	public void close();

	
}
