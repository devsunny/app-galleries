package com.asksunny.schema;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BottomUpEntityDataGenerator {

	private Entity entity;
	private List<BottomUpEntityDataGenerator> parentEntityGenerators;
	protected static SecureRandom rand = new SecureRandom(UUID.randomUUID().toString().getBytes());
	protected static final int MAX_SET_SIZE = 5;

	public List<Map<String, String>> generateDataSet() {
		int size = Math.abs(rand.nextInt(MAX_SET_SIZE));
		List<Map<String, String>> dataSet = new ArrayList<>();
		//Map<String, List<Map<String, String>>>  
		
		if (parentEntityGenerators != null && parentEntityGenerators.size() > 0) 
		{
			
			
		}
		return dataSet;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public List<BottomUpEntityDataGenerator> getParentEntityGenerators() {
		return parentEntityGenerators;
	}

	public void setParentEntityGenerators(List<BottomUpEntityDataGenerator> parentEntityGenerators) {
		this.parentEntityGenerators = parentEntityGenerators;
	}

	
}
