package com.asksunny.codegen.data;

import com.asksunny.schema.Entity;
import com.asksunny.schema.Schema;

public class BottomUpSchemaDataGenerator {

	private Schema schema;
	private SchemaDataConfig config;
	private String schemaUri;

	public void generateData() {
		schema.buildRelationship();
		for (Entity entity : schema.getAllEntities()) {
			if (!entity.hasReferencedBy()) {
				BottomUpEntityDataGenerator entityGen = EntityGeneratorFactory.createEntityGenerator(entity, config);
				entityGen.generateFullDataSet();
			}
		}
		for (BottomUpEntityDataGenerator entityGen : EntityGeneratorFactory.getAllCachedEntityGenerators()) {
			entityGen.generateFullDataSet();
		}

	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public BottomUpSchemaDataGenerator(Schema schema) {
		super();
		this.schema = schema;
	}

	public BottomUpSchemaDataGenerator() {

	}

	public String getSchemaUri() {
		return schemaUri;
	}

	public void setSchemaUri(String schemaUri) {
		this.schemaUri = schemaUri;
	}

	public SchemaDataConfig getConfig() {
		return config;
	}

	public void setConfig(SchemaDataConfig config) {
		this.config = config;
	}

	public void close() {
		for (BottomUpEntityDataGenerator entityGen : EntityGeneratorFactory.getAllCachedEntityGenerators()) {
			try {
				entityGen.close();
			} catch (Exception e) {
				;
			}
		}
	}

}
