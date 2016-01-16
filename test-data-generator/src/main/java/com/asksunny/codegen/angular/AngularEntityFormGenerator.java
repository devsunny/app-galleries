package com.asksunny.codegen.angular;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.codegen.CodeGenerator;
import com.asksunny.codegen.utils.ParamMapBuilder;
import com.asksunny.codegen.utils.TemplateUtil;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;

public class AngularEntityFormGenerator extends CodeGenerator {

	public AngularEntityFormGenerator(CodeGenConfig configuration, Entity entity) {
		super(configuration, entity);
	}

	@Override
	public void doCodeGen() throws IOException {
		writeCode(this.viewDir, String.format("%sForm.html", entity.getEntityObjectName()), genForm());
		writeCode(this.controllerDir, String.format("%sFormController.js", entity.getEntityObjectName()),
				genFormController());
	}

	// gen form Controller
	// gen save function
	// gen delete function if key exist

	public String genForm() throws IOException {
		StringBuilder fields = new StringBuilder();
		for (Field field : entity.getFields()) {
			AngularFieldGenerator fg = new AngularFieldGenerator(entity, field);
			fields.append(fg.genField());
		}
		String label = entity.getLabel() == null ? entity.getName() : entity.getLabel();
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("angularEntityForm.html.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("ANGULAR_APP_NAME", configuration.getAngularAppName())
						.addMapEntry("FORM_FIELDS", fields.toString())
						.addMapEntry("ENTITY_NAME", entity.getEntityObjectName()).addMapEntry("ENTITY_LABEL", label)
						.buildMap());
		return generated;
	}

	public String genNavigationItem() throws IOException {
		String label = entity.getLabel() == null ? entity.getName() : entity.getLabel();
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("AngularNavigationItem.html.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("ANGULAR_APP_NAME", configuration.getAngularAppName())
						.addMapEntry("NAVIGATION_STATE_NAME", entity.getEntityVarName())
						.addMapEntry("ENTITY_NAME", entity.getEntityObjectName()).addMapEntry("NAVIGATION_LABEL", label)
						.buildMap());
		return generated;
	}

	public String genFormController() throws IOException {
		Field kf = entity.getKeyField();
		String pkName = kf == null ? "uniqueId" : kf.getVarname();
		String generated = TemplateUtil
				.renderTemplate(IOUtils.toString(getClass().getResourceAsStream("angularEntityFormController.js.tmpl")),
						ParamMapBuilder.newBuilder().addMapEntry("PK_FIELD_VAR_NAME", pkName)
								.addMapEntry("ANGULAR_APP_NAME", configuration.getAngularAppName())
								.addMapEntry("INSERT_UPDATE_URI",
										String.format("/%s/%s", configuration.getWebappContext(),
												entity.getEntityVarName()))
						.addMapEntry("GET_REQUEST_URI",
								String.format("/%s/%s%s", configuration.getWebappContext(), entity.getEntityVarName(),
										generateInterpolateURL(entity.getKeyFields())))
						.addMapEntry("WEBCONTEXT", configuration.getWebappContext())
						.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
						.addMapEntry("ENTITY_VAR_NAME", entity.getEntityVarName())
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		return generated;
	}

	public String genAngularState() throws IOException {

		Field kf = entity.getKeyField();
		String pkName = kf == null ? "uniqueId" : kf.getVarname();

		String generated = TemplateUtil
				.renderTemplate(
						IOUtils.toString(
								getClass()
										.getResourceAsStream(
												"angularEntityFormState.js.tmpl")),
						ParamMapBuilder.newBuilder().addMapEntry("ANGULAR_APP_NAME", configuration.getAngularAppName())
								.addMapEntry("KEY_PARAMS", getKeyStateParameters())
								.addMapEntry("KEY_URI_PARAMS", getKeyParamURI())
								.addMapEntry("PK_FIELD_VAR_NAME", pkName)
								.addMapEntry("VIEW_NAME", entity.getEntityVarName())
								.addMapEntry("ENTITY_VAR_NAME", entity.getEntityVarName())
								.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
								.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		return generated;
	}

}
