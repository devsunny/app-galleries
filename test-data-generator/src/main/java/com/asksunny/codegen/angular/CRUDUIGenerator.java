package com.asksunny.codegen.angular;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.codegen.utils.JavaIdentifierUtil;
import com.asksunny.codegen.utils.ParamMapBuilder;
import com.asksunny.codegen.utils.TemplateUtil;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;

public class CRUDUIGenerator {

	private CodeGenConfig configuration;
	private Entity entity;

	public CRUDUIGenerator(CodeGenConfig configuration, Entity entity) {
		super();
		this.configuration = configuration;
		this.entity = entity;
	}

	public String genForm() throws IOException {
		StringBuilder fields = new StringBuilder();
		for (Field field : entity.getFields()) {
			AngularFieldGenerator fg = new AngularFieldGenerator(entity, field);
			fields.append(fg.genField()).append("\n");
		}
		String label = entity.getLabel() == null ? entity.getName() : entity.getLabel();
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("angularForm.html.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("FORM_FIELDS", fields.toString())
						.addMapEntry("ENTITY_NAME", entity.getEntityObjectName()).addMapEntry("ENTITY_LABEL", label)
						.buildMap());
		return generated;
	}

	public String genController() throws IOException {
		StringBuilder fields = new StringBuilder();
		for (Field field : entity.getFields()) {
			AngularFieldGenerator fg = new AngularFieldGenerator(entity, field);
			fields.append(fg.genField());
		}
		String label = entity.getLabel() == null ? entity.getName() : entity.getLabel();
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("angularForm.html.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("FORM_FIELDS", fields.toString())
						.addMapEntry("ENTITY_NAME", entity.getEntityObjectName()).addMapEntry("ENTITY_LABEL", label)
						.buildMap());
		return generated;
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
