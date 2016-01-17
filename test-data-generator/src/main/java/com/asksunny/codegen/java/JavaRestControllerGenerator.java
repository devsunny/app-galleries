package com.asksunny.codegen.java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.codegen.CodeGenerator;
import com.asksunny.codegen.utils.ParamMapBuilder;
import com.asksunny.codegen.utils.TemplateUtil;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.FieldDrillDownComparator;
import com.asksunny.schema.FieldGroupLevelComparator;
import com.asksunny.schema.parser.JdbcSqlTypeMap;

public class JavaRestControllerGenerator extends CodeGenerator {

	static final String INDENDENT_1 = "    ";
	static final String INDENDENT_2 = "        ";
	static final String INDENDENT_3 = "            ";
	static final String INDENDENT_4 = "                ";

	public JavaRestControllerGenerator(CodeGenConfig config, Entity entity) {
		super(config, entity);
	}

	public void doCodeGen() throws IOException {
		List<Field> keyFields = entity.getKeyFields();
		StringBuilder methods = new StringBuilder();
		if (keyFields.size() == 1) {
			doSingleKey(keyFields.get(0), methods);
		} else if (keyFields.size() > 1) {
			doMultipleKey(keyFields, methods);
		}
		doGroupBy(methods);
		doDrillDown(methods);
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("SpringRestJavaController.java.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("MAPPER_PACKAGE_NAME", configuration.getMapperPackageName())
						.addMapEntry("DOMAIN_PACKAGE_NAME", configuration.getDomainPackageName())
						.addMapEntry("REST_PACKAGE_NAME", configuration.getRestPackageName())
						.addMapEntry("MORE_REST_METHODS", methods.toString())
						.addMapEntry("ENTITY_VAR_NAME", entity.getEntityVarName())
						.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());

		String filePath = configuration.getRestPackageName().replaceAll("[\\.]", "/");
		writeCode(new File(configuration.getJavaBaseDir(), filePath),
				String.format("%sRestController.java", entity.getEntityObjectName()), generated);

	}

	protected void doMultipleKey(List<Field> keyFields, StringBuilder methods) {
		if (!entity.hasKeyField()) {
			return;
		}
		StringBuilder uri = new StringBuilder();
		List<String> params = new ArrayList<>();
		StringBuilder keyName = new StringBuilder();

		for (Field kf : keyFields) {
			uri.append("/{").append(kf.getVarname()).append("}");
			params.add(String.format("@PathVariable(\"%1$s\")%2$s %1$s", kf.getVarname(),
					JdbcSqlTypeMap.toJavaTypeName(kf)));
			keyName.append(kf.getObjectname());
		}
		String paramsString = StringUtils.join(params, ", ");
		methods.append(String.format("%2$s@RequestMapping(value=\"%1$s\", method = { RequestMethod.GET })\n",
				uri.toString(), INDENDENT_2));
		methods.append(INDENDENT_2).append("@ResponseBody\n");
		methods.append(String.format("%2$spublic %1$s get%1$sBy%4$s(%3$s){\n", entity.getEntityObjectName(),
				INDENDENT_2, paramsString, keyName.toString()));

		methods.append(String.format("%2$s%1$s %3$s = new  %1$s();\n", entity.getEntityObjectName(), INDENDENT_2,
				entity.getEntityVarName()));
		for (Field kf : keyFields) {
			methods.append(String.format("%2$s %3$s.set%1$s(%4$s);\n", kf.getObjectname(), INDENDENT_2,
					entity.getEntityVarName(), kf.getVarname()));
		}
		methods.append(String.format("%2$sreturn this.%3$sMapper.select%1$sBy%4$s(%3$s);", entity.getEntityObjectName(),
				INDENDENT_2, entity.getEntityVarName(), keyName.toString())).append("\n");
		methods.append(INDENDENT_2).append("}\n\n");

		methods.append(String.format("%1$s@RequestMapping(method = { RequestMethod.PUT })\n", INDENDENT_2));
		methods.append(INDENDENT_2).append("@ResponseBody\n");
		methods.append(String.format("%2$spublic int update%1$sBy%4$s(@RequestBody %1$s %3$s){\n",
				entity.getEntityObjectName(), INDENDENT_2, entity.getEntityVarName(), keyName.toString()));
		methods.append(String.format("%2$sreturn this.%3$sMapper.update%1$sBy%4$s(%3$s);\n",
				entity.getEntityObjectName(), INDENDENT_2, entity.getEntityVarName(), keyName.toString()));
		methods.append(INDENDENT_2).append("}\n\n");

		methods.append(String.format("%1$s@RequestMapping(method = { RequestMethod.DELETE })\n", INDENDENT_2));
		methods.append(INDENDENT_2).append("@ResponseBody\n");
		methods.append(String.format("%2$spublic int delete%1$sBy%4$s(@RequestBody %1$s %3$s){\n",
				entity.getEntityObjectName(), INDENDENT_2, entity.getEntityVarName(), keyName.toString()));
		methods.append(String.format("%2$sreturn this.%3$sMapper.delete%1$sBy%4$s(%3$s);\n",
				entity.getEntityObjectName(), INDENDENT_2, entity.getEntityVarName(), keyName.toString()));
		methods.append(INDENDENT_2).append("}\n\n");
	}

	protected void doSingleKey(Field keyField, StringBuilder methods) throws IOException {
		if (!entity.hasKeyField()) {
			return;
		}

		methods.append(String.format("%2$s@RequestMapping(value=\"/{%1$s}\", method = { RequestMethod.GET })\n",
				keyField.getVarname(), INDENDENT_2));
		methods.append(INDENDENT_2).append("@ResponseBody\n");
		methods.append(String.format("%2$spublic %1$s get%1$sBy%3$s(@PathVariable(\"%4$s\") %5$s %4$s){",
				entity.getEntityObjectName(), INDENDENT_2, keyField.getObjectname(), keyField.getVarname(),
				JdbcSqlTypeMap.toJavaTypeName(keyField))).append("\n");
		methods.append(String.format("%2$sreturn this.%6$sMapper.select%1$sBy%3$s(%4$s);", entity.getEntityObjectName(),
				INDENDENT_2, keyField.getObjectname(), keyField.getVarname(), JdbcSqlTypeMap.toJavaTypeName(keyField),
				entity.getEntityVarName())).append("\n");
		methods.append(INDENDENT_2).append("}\n\n");

		methods.append(String.format("%2$s@RequestMapping(method = { RequestMethod.PUT })\n", keyField.getVarname(),
				INDENDENT_2));
		methods.append(INDENDENT_2).append("@ResponseBody\n");
		methods.append(String.format("%2$spublic int update%1$sBy%4$s(@RequestBody %1$s %3$s){",
				entity.getEntityObjectName(), INDENDENT_2, entity.getEntityVarName(), keyField.getObjectname()))
				.append("\n");
		methods.append(String.format("%2$sreturn this.%6$sMapper.update%1$sBy%3$s(%6$s);", entity.getEntityObjectName(),
				INDENDENT_2, keyField.getObjectname(), keyField.getVarname(), JdbcSqlTypeMap.toJavaTypeName(keyField),
				entity.getEntityVarName())).append("\n");
		methods.append(INDENDENT_2).append("}\n\n");

		methods.append(String.format("%2$s@RequestMapping(method = { RequestMethod.DELETE })\n", keyField.getVarname(),
				INDENDENT_2));
		methods.append(INDENDENT_2).append("@ResponseBody\n");
		methods.append(String.format("%2$spublic int delete%1$sBy%4$s(@RequestBody %1$s %3$s){",
				entity.getEntityObjectName(), INDENDENT_2, entity.getEntityVarName(), keyField.getObjectname()))
				.append("\n");
		methods.append(String.format("%2$sreturn this.%6$sMapper.delete%1$sBy%3$s(%6$s);", entity.getEntityObjectName(),
				INDENDENT_2, keyField.getObjectname(), keyField.getVarname(), JdbcSqlTypeMap.toJavaTypeName(keyField),
				entity.getEntityVarName())).append("\n");
		methods.append(INDENDENT_2).append("}\n\n");
	}

	protected void doGroupBy(StringBuilder methods) throws IOException {
		if (!entity.hasGroupByFields()) {
			return;
		}
		List<Field> gbFields = entity.getGroupByFields();
		Collections.sort(gbFields, new FieldGroupLevelComparator());
		StringBuilder uri = new StringBuilder();
		uri.append("/groupby");
		for (Field kf : gbFields) {
			uri.append("/{").append(kf.getVarname()).append("}");
		}
		methods.append(String.format("%1$s@RequestMapping(value=\"%2$s\", method = { RequestMethod.GET })\n",
				INDENDENT_2, uri.toString()));
		methods.append(INDENDENT_2).append("@ResponseBody\n");
		if (gbFields.size() == 1) {
			Field keyField = gbFields.get(0);
			methods.append(String.format("%2$spublic java.util.List<%1$s> select%1$sGroupBy%3$s(%5$s %4$s){\n",
					entity.getEntityObjectName(), INDENDENT_2, keyField.getObjectname(), keyField.getVarname(),
					JdbcSqlTypeMap.toJavaTypeName(keyField)));
			methods.append(String.format("%2$s return this.%6$sMapper.select%1$sGroupBy%3$s(%4$s);\n",
					entity.getEntityObjectName(), INDENDENT_2, keyField.getObjectname(), keyField.getVarname(),
					JdbcSqlTypeMap.toJavaTypeName(keyField), entity.getEntityVarName()));

		} else if (gbFields.size() > 1) {
			StringBuilder buf = new StringBuilder();
			for (Field keyField : gbFields) {
				buf.append(keyField.getObjectname());
			}
			methods.append(String.format("%1$spublic java.util.List<%2$s> select%2$sGroupBy%4$s(%2$s %3$s){\n",
					INDENDENT_2, entity.getEntityObjectName(), entity.getEntityVarName(), buf.toString()));

			methods.append(String.format("%1$s return this.%3$sMapper.select%2$sGroupBy%4$s(%3$s){\n", INDENDENT_2,
					entity.getEntityObjectName(), entity.getEntityVarName(), buf.toString()));

		}
		methods.append(INDENDENT_2).append("}\n\n");
	}

	protected void doDrillDown(StringBuilder methods) throws IOException {
		if (!entity.hasDrillDownFields()) {
			return;
		}
		List<Field> ddFields = entity.getDrillDownFields();
		Collections.sort(ddFields, new FieldDrillDownComparator());

		StringBuilder restPath = new StringBuilder();
		restPath.append("/drilldown");
		for (int i = 0; i <=ddFields.size(); i++) {
			//Field dd0 = ddFields.get(i);
			String drilldownName = (i==ddFields.size())?"Detail":String.format("By%s", ddFields.get(i).getObjectname());
			if (i > 0) {
				restPath.append(String.format("/{%s}", ddFields.get(i-1).getVarname()));
			}
			String drillDownRestPath = restPath.toString();
			List<String> params = new ArrayList<>();
			for (int k = 1; k <=i; k++) {
				Field ddk = ddFields.get(k-1);
				params.add(String.format("@PathVariable(\"%2$s\")%1$s %2$s", JdbcSqlTypeMap.toJavaTypeName(ddk),
						ddk.getVarname()));
			}
			String paramstr = StringUtils.join(params, ", ");
			methods.append(String.format("%1$s@RequestMapping(value=\"%2$s\", method = { RequestMethod.GET })\n",
					INDENDENT_2, drillDownRestPath));
			methods.append(INDENDENT_2).append("@ResponseBody\n");
			methods.append(
					String.format("%1$s public java.util.List<%2$s> select%2$sDrilldown%4$s(%5$s){\n", INDENDENT_2,
							entity.getEntityObjectName(), entity.getEntityVarName(), drilldownName, paramstr));

			methods.append(String.format("%1$s %2$s %3$s = new %2$s();\n", INDENDENT_2, entity.getEntityObjectName(),
					entity.getEntityVarName()));
			for (int k = 1; k <=i; k++) {
				Field ddk = ddFields.get(k-1);
				methods.append(String.format("%1$s %3$s.set%4$s(%5$s);\n", INDENDENT_2, entity.getEntityObjectName(),
						entity.getEntityVarName(), ddk.getObjectname(), ddk.getVarname()));
			}
			methods.append(String.format("%1$s return this.%3$sMapper.select%2$sDrilldown%4$s(%3$s);\n", INDENDENT_2,
					entity.getEntityObjectName(), entity.getEntityVarName(), drilldownName));
			methods.append(INDENDENT_2).append("}\n\n");			
			

		}
	}

}
