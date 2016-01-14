package com.asksunny.codegen.java;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.codegen.utils.JavaIdentifierUtil;
import com.asksunny.codegen.utils.ParamMapBuilder;
import com.asksunny.codegen.utils.TemplateUtil;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.FieldDrillDownComparator;
import com.asksunny.schema.FieldGroupLevelComparator;
import com.asksunny.schema.parser.JdbcSqlTypeMap;

public class JavaMyBatisMapperGenerator {

	static final String INDENDENT_1 = "    ";
	static final String INDENDENT_2 = "        ";
	static final String INDENDENT_3 = "            ";
	static final String INDENDENT_4 = "                ";
	private CodeGenConfig config;
	private int fieldsSize = 0;
	private Entity entity;

	public JavaMyBatisMapperGenerator(CodeGenConfig config, Entity entity) {
		super();
		this.config = config;
		this.entity = entity;
	}

	public void doCodeGen() throws IOException {
		List<Field> keyFields = entity.getKeyFields();

		StringBuilder methods = new StringBuilder();

		methods.append(String.format("%2$svoid insert%1$s(%1$s %3$s);", entity.getEntityObjectName(), INDENDENT_2,
				entity.getEntityVarName())).append("\n");

		methods.append(
				String.format("%2$sjava.util.List<%1$s> select%1$s();", entity.getEntityObjectName(), INDENDENT_2))
				.append("\n");
		if (keyFields.size() == 1) {
			Field keyField = keyFields.get(0);
			methods.append(
					String.format("%2$s%1$s select%1$sBy%3$s(%5$s %4$s);", entity.getEntityObjectName(), INDENDENT_2,
							keyField.getObjectname(), keyField.getVarname(), JdbcSqlTypeMap.toJavaTypeName(keyField)))
					.append("\n");
			methods.append(String.format("%2$sint update%1$sBy%4$s(%1$s %3$s);", entity.getEntityObjectName(),
					INDENDENT_2, entity.getEntityVarName(), keyField.getObjectname())).append("\n");
			methods.append(String.format("%2$sint delete%1$sBy%4$s(%1$s %3$s);", entity.getEntityObjectName(),
					INDENDENT_2, entity.getEntityVarName(), keyField.getObjectname())).append("\n");

		} else if (keyFields.size() > 1) {
			methods.append(String.format("%2$s%1$s select%1$sByKey(%1$s %3$s);", entity.getEntityObjectName(),
					INDENDENT_2, entity.getEntityVarName())).append("\n");
			methods.append(String.format("%2$sint update%1$sByKey(%1$s %3$s);", entity.getEntityObjectName(),
					INDENDENT_2, entity.getEntityVarName())).append("\n");
			methods.append(String.format("%2$sint delete%1$sByKey(%1$s %3$s);", entity.getEntityObjectName(),
					INDENDENT_2, entity.getEntityVarName())).append("\n");
		}

		List<Field> gbFields = entity.getGroupByFields();
		Collections.sort(gbFields, new FieldGroupLevelComparator());
		if (gbFields.size() == 1) {
			Field keyField = gbFields.get(0);
			methods.append(String.format("%2$sjava.util.List<%1$s> select%1$sGroupBy%3$s(%5$s %4$s);",
					entity.getEntityObjectName(), INDENDENT_2, keyField.getObjectname(), keyField.getVarname(),
					JdbcSqlTypeMap.toJavaTypeName(keyField))).append("\n");

		} else if (gbFields.size() > 1) {
			StringBuilder buf = new StringBuilder();
			for (Field keyField : gbFields) {
				buf.append(keyField.getObjectname());
			}
			methods.append(String.format("%2$sjava.util.List<%1$s> select%1$sGroupBy%4$s(%1$s %3$s);",
					entity.getEntityObjectName(), INDENDENT_2, entity.getEntityVarName(), buf.toString())).append("\n");
		}

		List<Field> ddFields = entity.getDrillDownFields();
		Collections.sort(ddFields, new FieldDrillDownComparator());
		if (ddFields.size() > 0) {
			Field dd0 = ddFields.get(0);
			methods.append(String.format("%2$sjava.util.List<%1$s> select%1$sDrillDownBy%3$s(%5$s %4$s);",
					entity.getEntityObjectName(), INDENDENT_2, dd0.getObjectname(), dd0.getVarname(),
					JdbcSqlTypeMap.toJavaTypeName(dd0))).append("\n");
			
			String drillDownRestPath = String.format("/{%s}", dd0.getVarname());
			System.out.println(drillDownRestPath);
			String drillDownUIPath = String.format("/:%s", dd0.getVarname());
			System.out.println(drillDownUIPath);

		}
		if (ddFields.size() > 1) {
			Field dd1 = ddFields.get(0);
			StringBuilder restPath = new StringBuilder();
			restPath.append(String.format("/{%s}", dd1.getVarname()));
			StringBuilder uiPath =  new StringBuilder();
			uiPath.append(String.format("/:%s", dd1.getVarname()));
			
			for (int i = 1; i < ddFields.size(); i++) {
				Field dd0 = ddFields.get(i);
				methods.append(String.format("%2$sjava.util.List<%1$s> select%1$sDrillDownBy%3$s(%1$s %4$s);",
						entity.getEntityObjectName(), INDENDENT_2, dd0.getObjectname(), entity.getEntityVarName(),
						JdbcSqlTypeMap.toJavaTypeName(dd0), entity.getVarname())).append("\n");
				
				restPath.append(String.format("/{%s}", dd0.getVarname()));
				uiPath.append(String.format("/:%s", dd0.getVarname()));				
				String drillDownRestPath = restPath.toString();
				System.out.println(drillDownRestPath);
				String drillDownUIPath = uiPath.toString();
				System.out.println(drillDownUIPath);				
			}

		}

		if (ddFields.size() > 0) {
			StringBuilder restPath = new StringBuilder();			
			StringBuilder uiPath =  new StringBuilder();			
			for (int i = 0; i < ddFields.size(); i++){
				Field dd0 = ddFields.get(i);
				restPath.append(String.format("/{%s}", dd0.getVarname()));
				uiPath.append(String.format("/:%s", dd0.getVarname()));	
			}
			restPath.append("/viewName");
			uiPath.append("/:viewName");
			String drillDownRestPath = restPath.toString();
			System.out.println(drillDownRestPath);
			String drillDownUIPath = uiPath.toString();
			System.out.println(drillDownUIPath);	
			
			methods.append(String.format("%2$sjava.util.List<%1$s> select%1$sDrillDownDetail(%1$s %3$s);",
					entity.getEntityObjectName(), INDENDENT_2, entity.getVarname())).append("\n");

		}

		String generated = TemplateUtil
				.renderTemplate(
						IOUtils.toString(
								getClass()
										.getResourceAsStream(
												"myBatisJavaMapper.java.tmpl")),
						ParamMapBuilder.newBuilder().addMapEntry("MAPPER_PACKAGE_NAME", config.getMapperPackageName())
								.addMapEntry("DOMAIN_PACKAGE", config.getDomainPackageName())
								.addMapEntry("MAPPER_METHODS", methods.toString())
								.addMapEntry("ENTITY_VAR_NAME", entity.getEntityVarName())
								.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
								.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());

		System.out.println(generated);

	}

	public CodeGenConfig getConfig() {
		return config;
	}

	public void setConfig(CodeGenConfig config) {
		this.config = config;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

}
