package com.asksunny.codegen.angular;

import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
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
import com.asksunny.schema.FieldDrillDownComparator;

public class AngularEntityListGenerator extends CodeGenerator {

	public AngularEntityListGenerator(CodeGenConfig configuration, Entity entity) {
		super(configuration, entity);
	}

	@Override
	public void doCodeGen() throws IOException {
		writeCode(this.viewDir, String.format("%sList.html", entity.getEntityObjectName()), genTable(null));
		writeCode(this.controllerDir, String.format("%sListController.js", entity.getEntityObjectName()),
				genListController());
		genDrillDownView();
		genDrilldownController();
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
		StringBuilder allStates = new StringBuilder();

		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("angularEntityListState.js.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("ANGULAR_APP_NAME", configuration.getAngularAppName())
						.addMapEntry("VIEW_NAME", entity.getEntityVarName())
						.addMapEntry("ENTITY_VAR_NAME", entity.getEntityVarName())
						.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		allStates.append(generated);
		List<Field> drilldownFields = entity.getDrillDownFields();
		if (drilldownFields.size() > 0) {
			Collections.sort(drilldownFields, new FieldDrillDownComparator());
			StringBuilder uri = new StringBuilder();
			for (int i = 0; i <= drilldownFields.size(); i++) {
				if (i > 0) {
					uri.append(String.format("/:%s", drilldownFields.get(i - 1).getVarname()));
				}
				String ddname = i < drilldownFields.size()
						? String.format("Drilldown%s", drilldownFields.get(i).getObjectname()) : "DrilldownDetail";
				generated = TemplateUtil
						.renderTemplate(
								IOUtils.toString(
										getClass().getResourceAsStream(
												"angularEntityDrilldownState.js.tmpl")),
								ParamMapBuilder.newBuilder()
										.addMapEntry("ANGULAR_APP_NAME", configuration.getAngularAppName())
										.addMapEntry("DRILLDOWN_URL", uri.toString())
										.addMapEntry("DRILLDOWN_NAME", ddname)
										.addMapEntry("VIEW_NAME", entity.getEntityVarName())
										.addMapEntry("ENTITY_VAR_NAME", entity.getEntityVarName())
										.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
										.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
				allStates.append(generated);
			}
		}
		return allStates.toString();
	}

	public void genDrillDownView() throws IOException {
		List<Field> drilldownFields = entity.getDrillDownFields();
		if (drilldownFields == null || drilldownFields.size() == 0) {
			return;
		}

		Field groupFunct = entity.getGroupFunctionField();
		List<String> linkParams = new ArrayList<>();
		for (int i = 0; i <= drilldownFields.size(); i++) {
			String ddname = i < drilldownFields.size()
					? String.format("Drilldown%s", drilldownFields.get(i).getObjectname()) : "DrilldownDetail";
			if (i < drilldownFields.size()) {
				Field ddField = drilldownFields.get(i);
				linkParams.add(String.format("%1$s:listItem.%1$s", ddField.getVarname()));
				StringBuilder header = new StringBuilder();
				header.append(String.format("<th>%s</th>\n", ddField.getLabel()));
				StringBuilder body = new StringBuilder();
				String ddNext = i < (drilldownFields.size() - 1)
						? String.format("dashboard.%sDrilldown%s", entity.getEntityVarName(),
								drilldownFields.get(i + 1).getObjectname())
						: String.format("dashboard.%sDrilldownDetail", entity.getEntityVarName());
				body.append(String.format("<td><a ui-sref=\"%s({%s})\">{{listItem.%s}}</td>\n", ddNext,
						StringUtils.join(linkParams, ","), ddField.getVarname()));
				if (groupFunct != null) {
					header.append(String.format("<th>%s of %s</th>\n", groupFunct.getGroupFunction().toString(),
							groupFunct.getLabel()));
					body.append(String.format("<td>{{listItem.%s}}</td>\n", groupFunct.getVarname()));
				}

				String generated = TemplateUtil
						.renderTemplate(
								IOUtils.toString(getClass()
										.getResourceAsStream("angularEntityDrilldown.html.tmpl")),
								ParamMapBuilder.newBuilder().addMapEntry("TABLE_HEADER", header.toString())
										.addMapEntry("ITEMS_PER_PAGE", Integer.toString(entity.getItemsPerPage()))
										.addMapEntry("TABLE_BODY", body.toString())
										.addMapEntry("ENTITY_VAR_NAME", entity.getEntityVarName())
										.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
										.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());

				writeCode(this.viewDir, String.format("%s%s.html", entity.getEntityObjectName(), ddname), generated);
			} else {
				String generated = genTable(String.format("%sDrilldownCtrl", entity.getEntityObjectName()));
				writeCode(this.viewDir, String.format("%s%s.html", entity.getEntityObjectName(), ddname), generated);
			}

		}

	}

	public void genDrilldownController() throws IOException {
		List<Field> drilldownFields = entity.getDrillDownFields();
		if (drilldownFields == null || drilldownFields.size() == 0) {
			return;
		}
		StringBuilder ddParams = new StringBuilder();
		for (Field field : drilldownFields) {
			ddParams.append(TemplateUtil.renderTemplate(
					IOUtils.toString(getClass().getResourceAsStream("angularDrilldownStateParam.js.tmpl")),
					ParamMapBuilder.newBuilder().addMapEntry("FIELD_VAR_NAME", field.getVarname())
							.buildMap()));
		}

		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("angularEntityDrilldownController.js.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("ANGULAR_APP_NAME", configuration.getAngularAppName())
						.addMapEntry("GET_REQUEST_URI",
								String.format("/%s/%s/drilldown", configuration.getWebappContext(),
										entity.getEntityVarName()))
						.addMapEntry("STATE_PARAMETERS", ddParams.toString())
						.addMapEntry("WEBCONTEXT", configuration.getWebappContext())
						.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
						.addMapEntry("ENTITY_VAR_NAME", entity.getEntityVarName())
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		writeCode(this.controllerDir, String.format("%sDrilldownController.js", entity.getEntityObjectName()),
				generated);
	}

	public String genTable(String controllerName) throws IOException {
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
			if (link != null && field.isPrimaryKey()) {
				listItem = String.format("<a ui-sref=\"%s\">%s</a>", link, listItem);
			}
			tbody.append("<td>");
			tbody.append(listItem);
			tbody.append("</td>").append("\n");
		}

		String ListCtrl = controllerName == null ? String.format("%sListCtrl", entity.getEntityObjectName())
				: controllerName;
		String label = entity.getLabel() == null ? entity.getName() : entity.getLabel();
		
		String dilldownLink = "";
		if(entity.hasDrillDownFields()){
			Field ddf = entity.getDrillDownRoot();
			dilldownLink = TemplateUtil
					.renderTemplate(
							IOUtils.toString(
									getClass()
											.getResourceAsStream("angularDrilldownLink.html.tmpl")),
							ParamMapBuilder.newBuilder().addMapEntry("FIELD_OBJECT_NAME", ddf.getObjectname())
									.addMapEntry("FIELD_LABEL", ddf.getLabel())									
									.addMapEntry("ENTITY_VAR_NAME", entityVarName)
									.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
									.addMapEntry("ENTITY_LABEL", label).buildMap());
		}
		
		String generated = TemplateUtil
				.renderTemplate(
						IOUtils.toString(
								getClass()
										.getResourceAsStream("angularEntityList.html.tmpl")),
						ParamMapBuilder.newBuilder().addMapEntry("TABLE_HEADER", fields.toString())
								.addMapEntry("DRILLDOWN_LINK", dilldownLink)
								.addMapEntry("ENTITY_LIST_CONTROLLER", ListCtrl)
								.addMapEntry("ITEMS_PER_PAGE", Integer.toString(entity.getItemsPerPage()))
								.addMapEntry("TABLE_BODY", tbody.toString())
								.addMapEntry("ENTITY_VAR_NAME", entityVarName)
								.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
								.addMapEntry("ENTITY_LABEL", label).buildMap());
		return generated;

	}

	protected String detailLink() {
		List<Field> keyfields = entity.getKeyFields();
		if (keyfields.size() == 0)
			return null;
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
