package com.asksunny.codegen.angular;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.asksunny.codegen.utils.JavaIdentifierUtil;
import com.asksunny.codegen.utils.ParamMapBuilder;
import com.asksunny.codegen.utils.TemplateUtil;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;

public class AngularFieldGenerator {

	private Entity entity;
	private Field field;

	public AngularFieldGenerator(Entity entity, Field field) {
		super();
		this.entity = entity;
		this.field = field;
	}

	public String genField() throws IOException {
		String entityVarName = JavaIdentifierUtil.toVariableName(entity.getName());
		String fieldVarName = field.getVarname() != null ? field.getVarname()
				: JavaIdentifierUtil.toVariableName(field.getName());
		String label = field.getLabel() == null ? field.getName() : field.getLabel();

		String uiType = field.getUitype();
		String element = "input";
		String attrValue = field.isNullable() ? "" : "required";

		if (uiType == null) {
			uiType = "text";
		} else if (uiType.equalsIgnoreCase("datetime-local")) {
			uiType = "datetime_local";
		}

		HtmlFormInputType htmlFormInputType = HtmlFormInputType.TEXT;
		try {
			htmlFormInputType = HtmlFormInputType.valueOf(uiType.toUpperCase());
		} catch (Exception ex) {
			;
		}

		String generated = "";
		String HTML_INPUT_TYPE = "";
		switch (htmlFormInputType) {
		case TEXTAREA:
			generated = TemplateUtil.renderTemplate(
					IOUtils.toString(getClass().getResourceAsStream("angularTextarea.html.tmpl")),
					ParamMapBuilder.newBuilder().addMapEntry("FIELD_VAR_NAME", fieldVarName)
							.addMapEntry("HTML_TYPE", HTML_INPUT_TYPE).addMapEntry("ENTITY_VAR_NAME", entityVarName)
							.addMapEntry("FIELD_ATTRIBUTES", attrValue)
							.addMapEntry("FIELD_LABEL", label).buildMap());			
			break;
		case CHECKBOX:
			StringBuilder checkboxSelections = new StringBuilder();
			String[]  chkvals =   getEnumValues();
			for (int i = 0; i < chkvals.length; i++) {
				String opt = TemplateUtil.renderTemplate(
						IOUtils.toString(getClass().getResourceAsStream("angularCheckboxOpt.html.tmpl")),
						ParamMapBuilder.newBuilder().addMapEntry("OPTION_VALUE", chkvals[i])
						.addMapEntry("ENTITY_VAR_NAME", entityVarName)
						.addMapEntry("FIELD_VAR_NAME_SEQ", String.format("%s%02d", fieldVarName, i+1))
						.addMapEntry("FIELD_VAR_NAME", fieldVarName)
								.addMapEntry("FIELD_LABEL", label).buildMap());
				checkboxSelections.append(opt).append("\n");
			}
			generated = TemplateUtil.renderTemplate(
					IOUtils.toString(getClass().getResourceAsStream("angularCheckbox.html.tmpl")),
					ParamMapBuilder.newBuilder().addMapEntry("SELECTIONS", checkboxSelections.toString())
							.addMapEntry("FIELD_LABEL", label).buildMap());
			break;
		case RADIO:
			StringBuilder radioSelections = new StringBuilder();
			String[]  enumvals =   getEnumValues();
			for (int i = 0; i < enumvals.length; i++) {
				String opt = TemplateUtil.renderTemplate(
						IOUtils.toString(getClass().getResourceAsStream("angularRadioOpt.html.tmpl")),
						ParamMapBuilder.newBuilder().addMapEntry("OPTION_VALUE", enumvals[i])
						.addMapEntry("ENTITY_VAR_NAME", entityVarName)
						.addMapEntry("FIELD_VAR_NAME_SEQ", String.format("%s%02d", fieldVarName, i+1))
						.addMapEntry("FIELD_VAR_NAME", fieldVarName)
								.addMapEntry("FIELD_LABEL", label).buildMap());
				radioSelections.append(opt).append("\n");
			}
			generated = TemplateUtil.renderTemplate(
					IOUtils.toString(getClass().getResourceAsStream("angularRadio.html.tmpl")),
					ParamMapBuilder.newBuilder().addMapEntry("SELECTIONS", radioSelections.toString())
							.addMapEntry("FIELD_LABEL", label).buildMap());
			break;
		case SELECT:
			StringBuilder selectSelections = new StringBuilder();
			String[]  enumSvals =   getEnumValues();
			for (int i = 0; i < enumSvals.length; i++) {
				String opt = TemplateUtil.renderTemplate(
						IOUtils.toString(getClass().getResourceAsStream("angularSelectOpt.html.tmpl")),
						ParamMapBuilder.newBuilder().addMapEntry("OPTION_VALUE", enumSvals[i])
						.buildMap());
				selectSelections.append(opt).append("\n");
			}
			generated = TemplateUtil.renderTemplate(
					IOUtils.toString(getClass().getResourceAsStream("angularSelect.html.tmpl")),
					ParamMapBuilder.newBuilder().addMapEntry("FIELD_LABEL", label)
					.addMapEntry("ENTITY_VAR_NAME", entityVarName)
					.addMapEntry("FIELD_VAR_NAME", fieldVarName)
					.addMapEntry("SELECTIONS", selectSelections.toString())
					.buildMap());
			break;
		default:
			HTML_INPUT_TYPE = uiType;
			if (htmlFormInputType == HtmlFormInputType.DATETIME_LOCAL) {
				HTML_INPUT_TYPE = "datetime_local";
			}
			generated = TemplateUtil.renderTemplate(
					IOUtils.toString(getClass().getResourceAsStream("angularField.input.html.tmpl")),
					ParamMapBuilder.newBuilder().addMapEntry("FIELD_VAR_NAME", fieldVarName)
							.addMapEntry("HTML_TYPE", HTML_INPUT_TYPE).addMapEntry("ENTITY_VAR_NAME", entityVarName)
							.addMapEntry("FIELD_ATTRIBUTES", attrValue).addMapEntry("FIELD_INPUT_TYPE", element)
							.addMapEntry("FIELD_LABEL", label).buildMap());
			break;
		}
		return generated;

	}

	protected String[] getEnumValues() {
		return field.getEnumValues() == null ? new String[0] : field.getEnumValues().split("\\s*\\|\\s*");
	}

}
