package com.asksunny.codegen.java;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.codegen.CodeGenerator;
import com.asksunny.codegen.utils.ParamMapBuilder;
import com.asksunny.codegen.utils.TemplateUtil;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.FieldDrillDownComparator;
import com.asksunny.schema.FieldGroupLevelComparator;
import com.asksunny.schema.parser.JdbcSqlTypeMap;

public class JavaMyBatisMapperGenerator extends CodeGenerator {

	static final String INDENDENT_1 = "    ";
	static final String INDENDENT_2 = "        ";
	static final String INDENDENT_3 = "            ";
	static final String INDENDENT_4 = "                ";

	public JavaMyBatisMapperGenerator(CodeGenConfig config, Entity entity) {
		super(config, entity);
	}

	public void doCodeGen() throws IOException {
		List<Field> keyFields = entity.getKeyFields();
		if (keyFields.size() == 0 && entity.hasUniqueField()) {
			keyFields = entity.getUniqueFields();
		}

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
			StringBuilder keyName = new StringBuilder();
			for (Field field : keyFields) {
				keyName.append(field.getObjectname());
			}

			methods.append(String.format("%2$s%1$s select%1$sBy%4$s(%1$s %3$s);", entity.getEntityObjectName(),
					INDENDENT_2, entity.getEntityVarName(), keyName.toString())).append("\n");
			methods.append(String.format("%2$sint update%1$sBy%4$s(%1$s %3$s);", entity.getEntityObjectName(),
					INDENDENT_2, entity.getEntityVarName(), keyName.toString())).append("\n");
			methods.append(String.format("%2$sint delete%1$sBy%4$s(%1$s %3$s);", entity.getEntityObjectName(),
					INDENDENT_2, entity.getEntityVarName(), keyName.toString())).append("\n");
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
			methods.append(String.format("%1$sjava.util.List<%2$s> select%2$sDrilldownBy%4$s(%2$s %3$s);",
					INDENDENT_2, entity.getEntityObjectName(), entity.getEntityVarName(), dd0.getObjectname(), dd0.getVarname(),
					JdbcSqlTypeMap.toJavaTypeName(dd0))).append("\n");

		}
		if (ddFields.size() > 1) {
			Field dd1 = ddFields.get(0);
			StringBuilder restPath = new StringBuilder();
			restPath.append(String.format("/{%s}", dd1.getVarname()));
			StringBuilder uiPath = new StringBuilder();
			uiPath.append(String.format("/:%s", dd1.getVarname()));

			for (int i = 1; i < ddFields.size(); i++) {
				Field dd0 = ddFields.get(i);
				methods.append(String.format("%2$sjava.util.List<%1$s> select%1$sDrilldownBy%3$s(%1$s %4$s);",
						entity.getEntityObjectName(), INDENDENT_2, dd0.getObjectname(), entity.getEntityVarName(),
						JdbcSqlTypeMap.toJavaTypeName(dd0), entity.getVarname())).append("\n");

			}

		}

		if (ddFields.size() > 0) {
			StringBuilder restPath = new StringBuilder();
			StringBuilder uiPath = new StringBuilder();
			for (int i = 0; i < ddFields.size(); i++) {
				Field dd0 = ddFields.get(i);
				restPath.append(String.format("/{%s}", dd0.getVarname()));
				uiPath.append(String.format("/:%s", dd0.getVarname()));
			}

			methods.append(String.format("%1$sjava.util.List<%2$s> select%2$sDrilldownDetail(%2$s %3$s);\n",
					INDENDENT_2, entity.getEntityObjectName(), entity.getEntityVarName()));

		}

		String generated = TemplateUtil
				.renderTemplate(
						IOUtils.toString(
								getClass()
										.getResourceAsStream("myBatisJavaMapper.java.tmpl")),
						ParamMapBuilder.newBuilder()
								.addMapEntry("MAPPER_PACKAGE_NAME", configuration.getMapperPackageName())
								.addMapEntry("DOMAIN_PACKAGE", configuration.getDomainPackageName())
								.addMapEntry("MAPPER_METHODS", methods.toString())
								.addMapEntry("ENTITY_VAR_NAME", entity.getEntityVarName())
								.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
								.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());

		String mapperPath = configuration.getMapperPackageName().replaceAll("[\\.]", "/");
		writeCode(new File(configuration.getJavaBaseDir(), mapperPath),
				String.format("%sMapper.java", entity.getEntityObjectName()), generated);

	}

}
