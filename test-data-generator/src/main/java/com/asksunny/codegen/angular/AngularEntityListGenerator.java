package com.asksunny.codegen.angular;

import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.codegen.CodeGenerator;
import com.asksunny.codegen.utils.JavaIdentifierUtil;
import com.asksunny.codegen.utils.ParamMapBuilder;
import com.asksunny.codegen.utils.TemplateUtil;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;

public class AngularEntityListGenerator extends CodeGenerator {

	public AngularEntityListGenerator(CodeGenConfig configuration, Entity entity) {
		super(configuration, entity);
	}

	@Override
	public void doCodeGen() throws IOException {
		writeCode(this.viewDir, String.format("%sList.html", entity.getEntityObjectName()), genTable());
		writeCode(this.controllerDir, String.format("%sListController.js", entity.getEntityObjectName()),
				genListController());
	}

	// gen List Controller
	// gen the link to detail if key exist
	// gen the link to form for insert
	// gen delete function if key exist

	public String genNavigationItem() throws IOException {
		String generated = TemplateUtil
				.renderTemplate(IOUtils.toString(getClass().getResourceAsStream("AngularNavigationItem.html.tmpl")),
						ParamMapBuilder.newBuilder().addMapEntry("ANGULAR_APP_NAME", configuration.getAngularAppName())
								.addMapEntry("NAVIGATION_STATE_NAME",
										String.format("%sList", entity.getEntityVarName()))
						.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
						.addMapEntry("NAVIGATION_LABEL", entity.getLabel()).buildMap());
		return generated;
	}

	public String genAngularState() throws IOException {
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("angularEntityListState.js.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("ANGULAR_APP_NAME", configuration.getAngularAppName())
						.addMapEntry("VIEW_NAME", entity.getEntityVarName())
						.addMapEntry("ENTITY_VAR_NAME", entity.getEntityVarName())
						.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		return generated;
	}
	
	

	public String genTable() throws IOException {
		StringBuilder fields = new StringBuilder();
		StringBuilder tbody = new StringBuilder();
		String entityVarName = JavaIdentifierUtil.toVariableName(entity.getName());
		String link = detailLink();
		
		for (Field field : entity.getFields()) {
			fields.append("<th>").append(field.getLabel()).append("</th>").append("\n");	
			String format = "";
			if (field.getFormat() != null) {
				if (field.getJdbcType() == Types.DATE | field.getJdbcType() == Types.TIME
						|| field.getJdbcType() == Types.TIMESTAMP) {
					String fmt = field.getFormat();
					if (fmt == null) {
						fmt = field.getJdbcType() == Types.DATE ? "yyyy-MM-dd"
								: (field.getJdbcType() == Types.TIMESTAMP) ? "yyyy-MM-dd HH:mm:ss" : "HH:mm:ss";
					}
					format = String.format(" | date: \"%s\"", fmt);
				}				
			}			
			String listItem = String.format("{{listItem.%s%s}}", field.getVarname(), format);
			if(link!=null && field.isPrimaryKey()){
				listItem = String.format("<a ui-sref=\"%s\">%s</a>",link, listItem);				
			}
			tbody.append("<td>");
			tbody.append(listItem);				
			tbody.append("</td>").append("\n");
		}
		String label = entity.getLabel() == null ? entity.getName() : entity.getLabel();
		String generated = TemplateUtil
				.renderTemplate(
						IOUtils.toString(
								getClass()
										.getResourceAsStream("angularEntityList.html.tmpl")),
						ParamMapBuilder.newBuilder().addMapEntry("TABLE_HEADER", fields.toString())
								.addMapEntry("ITEMS_PER_PAGE", Integer.toString(entity.getItemsPerPage()))
								.addMapEntry("TABLE_BODY", tbody.toString())
								.addMapEntry("ENTITY_VAR_NAME", entityVarName)
								.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
								.addMapEntry("ENTITY_LABEL", label).buildMap());
		return generated;
		
		
	}
	
	
	protected String detailLink()
	{		
		List<Field> keyfields = entity.getKeyFields();
		if(keyfields.size()==0) return null;
		List<String> parms = new ArrayList<>();
		for (Field field : keyfields) {
			parms.add(String.format("%1$s:listItem.%1$s", field.getVarname()));
		}		
		String aref = String.format("dashboard.%sForm({%s})", entity.getEntityVarName(), StringUtils.join(parms, ","));	
		return aref;
	}
	

	public String genListController() throws IOException {
		Field kf = entity.getKeyField();
		String pkName = kf == null ? "uniqueId" : kf.getVarname();
		String generated = TemplateUtil
				.renderTemplate(IOUtils.toString(getClass().getResourceAsStream("angularEntityListController.js.tmpl")),
						ParamMapBuilder.newBuilder().addMapEntry("PK_FIELD_VAR_NAME", pkName)
								.addMapEntry("ANGULAR_APP_NAME", configuration.getAngularAppName())
								.addMapEntry("GET_REQUEST_URI",
										String.format("/%s/%s", configuration.getWebappContext(),
												entity.getEntityVarName()))
						.addMapEntry("WEBCONTEXT", configuration.getWebappContext())
						.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
						.addMapEntry("ENTITY_VAR_NAME", entity.getEntityVarName())
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		return generated;
	}

}
