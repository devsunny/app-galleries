package com.asksunny.codegen.angular;

import java.io.PrintWriter;
import java.io.StringWriter;

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

	String genForm() {
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);

		out.flush();
		return buf.toString();
	}

	String genController() {
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);
		
		
		
		
		out.flush();
		return buf.toString();
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
