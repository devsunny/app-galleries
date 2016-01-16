package com.asksunny.codegen.angular;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;

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

	private static final String CONTROLLERS = "scripts/controllers";
	private static final String VIEWS = "views";

	public CRUDUIGenerator(CodeGenConfig configuration, Entity entity) {
		super();
		this.configuration = configuration;
		this.entity = entity;
	}

	public String genTable() throws IOException {
		StringBuilder fields = new StringBuilder();
		StringBuilder tbody = new StringBuilder();
		String entityVarName = JavaIdentifierUtil.toVariableName(entity
				.getName());
		for (Field field : entity.getFields()) {
			fields.append("<th>").append(field.getLabel()).append("</th>")
					.append("\n");
			tbody.append("<td>").append("{{listItem.")
					.append(field.getVarname());
			if (field.getFormat() != null) {
				if (field.getJdbcType() == Types.DATE
						| field.getJdbcType() == Types.TIME
						|| field.getJdbcType() == Types.TIMESTAMP) {
					tbody.append(" | date: ").append("\"")
							.append(field.getFormat()).append("\"");
				}
			}
			tbody.append("}}</td>").append("\n");
		}
		String label = entity.getLabel() == null ? entity.getName() : entity
				.getLabel();
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream(
						"angularTable.html.tmpl")),
				ParamMapBuilder
						.newBuilder()
						.addMapEntry("TABLE_HEADER", fields.toString())
						.addMapEntry("TABLE_BODY", tbody.toString())
						.addMapEntry("ENTITY_VAR_NAME", entityVarName)
						.addMapEntry("ENTITY_NAME",
								entity.getEntityObjectName())
						.addMapEntry("ENTITY_LABEL", label).buildMap());
		return generated;
	}

	public String genAngularRoute() throws IOException {

		Field kf = entity.getKeyField();
		String pkName = kf == null ? "uniqueId" : kf.getVarname();

		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream(
						"angularState.js.tmpl")),
				ParamMapBuilder
						.newBuilder()
						.addMapEntry("ANGULAR_APP_NAME",
								configuration.getAngularAppName())
						.addMapEntry("PK_FIELD_VAR_NAME", pkName)
						.addMapEntry("VIEW_NAME",
								entity.getEntityVarName())
						.addMapEntry("ENTITY_VAR_NAME",
								entity.getEntityVarName())
						.addMapEntry("ENTITY_NAME",
								entity.getEntityObjectName())
						.addMapEntry("ENTITY_LABEL", entity.getLabel())
						.buildMap());
		return generated;
	}

	public String genForm() throws IOException {
		StringBuilder fields = new StringBuilder();
		for (Field field : entity.getFields()) {
			AngularFieldGenerator fg = new AngularFieldGenerator(entity, field);
			fields.append(fg.genField());
		}

		String label = entity.getLabel() == null ? entity.getName() : entity
				.getLabel();
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream(
						"angularForm.html.tmpl")),
				ParamMapBuilder
						.newBuilder()
						.addMapEntry("ANGULAR_APP_NAME",
								configuration.getAngularAppName())
						.addMapEntry("FORM_FIELDS", fields.toString())
						.addMapEntry("ENTITY_NAME",
								entity.getEntityObjectName())
						.addMapEntry("ENTITY_LABEL", label).buildMap());
		return generated;
	}

	public String genController() throws IOException {

		Field kf = entity.getKeyField();
		String pkName = kf == null ? "uniqueId" : kf.getVarname();
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream(
						"angularControlller.js.tmpl")),
				ParamMapBuilder
						.newBuilder()
						.addMapEntry("PK_FIELD_VAR_NAME", pkName)
						.addMapEntry("ANGULAR_APP_NAME",
								configuration.getAngularAppName())
						.addMapEntry("WEBCONTEXT",
								configuration.getWebappContext())
						.addMapEntry("ENTITY_NAME",
								entity.getEntityObjectName())
						.addMapEntry("ENTITY_VAR_NAME",
								entity.getEntityVarName())
						.addMapEntry("ENTITY_LABEL", entity.getLabel())
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
