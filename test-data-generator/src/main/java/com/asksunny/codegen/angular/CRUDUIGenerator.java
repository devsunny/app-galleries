package com.asksunny.codegen.angular;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.schema.Entity;

public class CRUDUIGenerator {

	private CodeGenConfig configuration;
	private Entity entity;

	public CRUDUIGenerator(CodeGenConfig configuration, Entity entity) {
		super();
		this.configuration = configuration;
		this.entity = entity;
	}
	
	
	
	

	public CodeGenConfig getConfiguration() {
		return configuration;
	}

	public void setConfiguration(CodeGenConfig configuration) {
		this.configuration = configuration;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

}
