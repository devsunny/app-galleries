package com.asksunny.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class EntityGeneratorFactory {

	private static Map<String, BottomUpEntityDataGenerator> cachedEntityGenerators = new ConcurrentHashMap<>();

	public static BottomUpEntityDataGenerator createEntityGenerator(Entity entity, SchemaDataConfig config) {
		BottomUpEntityDataGenerator entityGen = cachedEntityGenerators.get(entity.getName().toUpperCase());
		if (entityGen == null) {
			entityGen = new BottomUpEntityDataGenerator(entity);
			entityGen.setConfig(config);
			entityGen.setFieldGenerators(FieldGeneratorFactory.createFieldGenerator(entity));
			entityGen.open();
			cachedEntityGenerators.put(entity.getName().toUpperCase(), entityGen);
		}
		return entityGen;
	}

	public static List<BottomUpEntityDataGenerator> getAllCachedEntityGenerators() {
		List<BottomUpEntityDataGenerator> egens = new ArrayList<>(cachedEntityGenerators.values());
		return egens;
	}

	public EntityGeneratorFactory() {

	}

}
