package com.asksunny.codegen.java;

import java.io.File;
import java.io.IOException;
import java.sql.Types;
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
import com.asksunny.schema.FieldOrderComparator;
import com.asksunny.schema.parser.JdbcSqlTypeMap;

public class MyBatisXmlEntityGenerator extends CodeGenerator {

	static final String INDENDENT_1 = "    ";
	static final String INDENDENT_2 = "        ";
	static final String INDENDENT_3 = "            ";
	static final String INDENDENT_4 = "                ";
	static final String NEWLINE = "\n";

	private String javaEntityName = null;
	private String javaEntityVarName = null;
	private List<Field> primaryKeys = new ArrayList<>();
	private List<Field> allFields = null;
	private List<String> allFieldNames = new ArrayList<>();
	private List<String> allFieldDbNames = new ArrayList<>();
	private int fieldsSize = 0;

	public void doCodeGen() throws IOException {
		StringBuilder xmlmapper = new StringBuilder();
		xmlmapper.append(genResultMap()).append(NEWLINE);
		xmlmapper.append(genInsert()).append(NEWLINE);
		xmlmapper.append(genSelectBasic()).append(NEWLINE);

		if (entity.hasKeyField() || entity.hasUniqueField()) {
			xmlmapper.append(genSelectByKey()).append(NEWLINE);
			xmlmapper.append(genUpdate()).append(NEWLINE);
			xmlmapper.append(genDelete()).append(NEWLINE);
		}

		if (entity.hasGroupByFields()) {
			xmlmapper.append(genSelectByGroup()).append(NEWLINE);
		}
		if (entity.hasDrillDownFields()) {
			xmlmapper.append(genDrilldown()).append(NEWLINE);
		}

		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("MyBatis.xml.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("MAPPER_PACKAGE_NAME", configuration.getMapperPackageName())
						.addMapEntry("DOMAIN_PACKAGE_NAME", configuration.getDomainPackageName())
						.addMapEntry("REST_PACKAGE_NAME", configuration.getRestPackageName())
						.addMapEntry("SQLMAP", xmlmapper.toString())
						.addMapEntry("ENTITY_VAR_NAME", entity.getEntityVarName())
						.addMapEntry("ENTITY_NAME", entity.getEntityObjectName())
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		String filePath = configuration.getMapperPackageName().replaceAll("[\\.]", "/");
		writeCode(new File(configuration.getMyBatisXmlBaseDir(), filePath),
				String.format("%sMapper.xml", entity.getEntityObjectName()), generated);
	}

	public String genInsert() throws IOException {
		List<String> collist = new ArrayList<>();
		List<String> vallist = new ArrayList<>();
		for (int i = 0; i < fieldsSize; i++) {
			Field fd = this.allFields.get(i);
			if (!fd.isAutogen()) {
				collist.add(fd.getName());
				if ((fd.getJdbcType() == Types.BIT || fd.getJdbcType() == Types.BOOLEAN)) {
					vallist.add(String.format("#{%s,javaType=boolean,jdbcType=%s}", fd.getVarname(),
							JdbcSqlTypeMap.getJdbcTyepName(fd.getJdbcType())));
				} else {
					vallist.add(String.format("#{%s,jdbcType=%s}", fd.getVarname(),
							JdbcSqlTypeMap.getJdbcTyepName(fd.getJdbcType())));

				}

			}
		}
		String cols = StringUtils.join(collist, ",");
		String vals = StringUtils.join(vallist, ",\n" + INDENDENT_2);
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("myBatis.insert.xml.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("FIELD_SELECT_LIST", cols)
						.addMapEntry("INSERT_VALUES_LIST", vals).addMapEntry("TABLE_NAME", entity.getName())
						.addMapEntry("ENTITY_VAR_NAME", javaEntityVarName).addMapEntry("ENTITY_NAME", javaEntityName)
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		return generated;
	}

	public String genUpdate() throws IOException {
		boolean hasKey = entity.hasKeyField();
		boolean hasUnique = entity.hasUniqueField();
		if (!(hasKey || hasUnique)) {
			return null;
		}
		List<String> keyCols = new ArrayList<>();
		List<String> updateList = new ArrayList<>();
		int knum = 0;
		StringBuffer keyName = new StringBuffer();
		for (int i = 0; i < fieldsSize; i++) {
			Field fd = this.allFields.get(i);
			if (!fd.isAutogen()) {
				updateList.add(String.format("%s=#{%s,jdbcType=%s}", fd.getName(), fd.getVarname(),
						JdbcSqlTypeMap.getJdbcTyepName(fd.getJdbcType())));
			}

			if (hasKey) {
				if (fd.isPrimaryKey()) {
					keyCols.add(String.format("%s=#{%s,jdbcType=%s}", fd.getName(), fd.getVarname(),
							JdbcSqlTypeMap.getJdbcTyepName(fd.getJdbcType())));
					keyName.append(fd.getObjectname());
					knum++;
				}
			} else if (hasUnique) {
				if (fd.isUnique()) {
					keyCols.add(String.format("%s=#{%s,jdbcType=%s}", fd.getName(), fd.getVarname(),
							JdbcSqlTypeMap.getJdbcTyepName(fd.getJdbcType())));
					keyName.append(fd.getObjectname());
					knum++;
				}
			}

		}
		String keyType = knum > 1 ? javaEntityName : JdbcSqlTypeMap.toJavaTypeName(entity.getKeyField());
		String updateLists = StringUtils.join(updateList, ",\n        ");
		String whereClause = StringUtils.join(keyCols, "\n        AND ");
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("myBatis.update.xml.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("KEY_TYPE", keyType)
						.addMapEntry("KEY_NAME", keyName.toString()).addMapEntry("UPDATE_FIELD_LIST", updateLists)
						.addMapEntry("WHERE_CLAUSE", whereClause).addMapEntry("TABLE_NAME", entity.getName())
						.addMapEntry("ENTITY_VAR_NAME", javaEntityVarName).addMapEntry("ENTITY_NAME", javaEntityName)
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		return generated;
	}

	public String genDelete() throws IOException {
		boolean hasKey = entity.hasKeyField();
		boolean hasUnique = entity.hasUniqueField();
		if (!hasKey && !hasUnique) {
			return null;
		}
		List<String> keyCols = new ArrayList<>();
		int knum = 0;
		StringBuffer keyName = new StringBuffer();
		for (int i = 0; i < fieldsSize; i++) {
			Field fd = this.allFields.get(i);
			if (hasKey) {
				if (fd.isPrimaryKey()) {
					keyCols.add(String.format("%s=#{%s,jdbcType=%s}", fd.getName(), fd.getVarname(),
							JdbcSqlTypeMap.getJdbcTyepName(fd.getJdbcType())));
					keyName.append(fd.getObjectname());
					knum++;
				}
			} else if (hasUnique) {
				if (fd.isUnique()) {
					keyCols.add(String.format("%s=#{%s,jdbcType=%s}", fd.getName(), fd.getVarname(),
							JdbcSqlTypeMap.getJdbcTyepName(fd.getJdbcType())));
					keyName.append(fd.getObjectname());
					knum++;
				}
			}
		}
		String keyType = knum > 1 ? javaEntityName : JdbcSqlTypeMap.toJavaTypeName(entity.getKeyField());
		String whereClause = StringUtils.join(keyCols, "\n        AND ");
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("myBatis.delete.xml.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("KEY_TYPE", keyType).addMapEntry("WHERE_CLAUSE", whereClause)
						.addMapEntry("KEY_NAME", keyName.toString()).addMapEntry("TABLE_NAME", entity.getName())
						.addMapEntry("ENTITY_VAR_NAME", javaEntityVarName).addMapEntry("ENTITY_NAME", javaEntityName)
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		return generated;
	}

	public String genSelectBasic() throws IOException {
		List<Field> sortFields = new ArrayList<>(this.allFields);
		Collections.sort(sortFields, new FieldOrderComparator());
		List<String> collist = new ArrayList<>();
		for (int i = 0; i < fieldsSize; i++) {
			Field fd = sortFields.get(i);
			collist.add(fd.getName());
		}
		String orderby = entity.getOrderBy() == null ? "" : ("ORDER BY " + entity.getOrderBy());
		String cols = StringUtils.join(collist, ",");
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("myBatis.select.xml.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("FIELD_SELECT_LIST", cols).addMapEntry("ORDER_BY", orderby)
						.addMapEntry("TABLE_NAME", entity.getName()).addMapEntry("ENTITY_VAR_NAME", javaEntityVarName)
						.addMapEntry("ENTITY_NAME", javaEntityName).addMapEntry("ENTITY_LABEL", entity.getLabel())
						.buildMap());
		return generated;
	}

	public String genSelectByKey() throws IOException {
		if (!this.entity.hasKeyField()) {
			return null;
		}
		List<Field> sortFields = new ArrayList<>(this.allFields);
		Collections.sort(sortFields, new FieldOrderComparator());
		List<String> collist = new ArrayList<>();
		List<Field> keyFields = new ArrayList<>();
		for (int i = 0; i < fieldsSize; i++) {
			Field fd = sortFields.get(i);
			collist.add(fd.getName());
			if (fd.isPrimaryKey()) {
				keyFields.add(fd);
			}
		}
		String keyType = keyFields.size() > 1 ? javaEntityName : JdbcSqlTypeMap.toJavaTypeName(keyFields.get(0));
		List<String> whereList = new ArrayList<>();
		StringBuffer keyName = new StringBuffer();
		for (Field fd : keyFields) {
			whereList.add(String.format("%s=#{%s,jdbcType=%s}", fd.getName(), fd.getVarname(),
					JdbcSqlTypeMap.getJdbcTyepName(fd.getJdbcType())));
			keyName.append(fd.getObjectname());
		}
		String whereClause = StringUtils.join(whereList, " AND\n");
		String orderby = entity.getOrderBy() == null ? "" : ("ORDER BY " + entity.getOrderBy());
		String cols = StringUtils.join(collist, ",");
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("myBatis.select.bykey.xml.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("FIELD_SELECT_LIST", cols).addMapEntry("KEY_TYPE", keyType)
						.addMapEntry("KEY_NAME", keyName.toString()).addMapEntry("WHERE_KEY_FIELD", whereClause)
						.addMapEntry("ORDER_BY", orderby).addMapEntry("TABLE_NAME", entity.getName())
						.addMapEntry("ENTITY_VAR_NAME", javaEntityVarName).addMapEntry("ENTITY_NAME", javaEntityName)
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		return generated;
	}

	public String genSelectByGroup() throws IOException {
		List<Field> sortFields = new ArrayList<>(this.allFields);
		Collections.sort(sortFields, new FieldOrderComparator());
		List<String> collist = new ArrayList<>();
		List<Field> groupList = entity.getGroupByFields();
		Field gffd = entity.getGroupFunctionField();
		if (gffd != null) {
			collist.add(String.format("%1$s(%2$s) as %2$s", gffd.getGroupFunction().toString(), gffd.getName()));
		}
		if (groupList.size() > 0) {
			Collections.sort(groupList, new FieldGroupLevelComparator());
		}
		int gpSize = groupList.size();
		StringBuilder orderby = new StringBuilder();
		StringBuilder groupBy = new StringBuilder();
		StringBuilder groupByKey = new StringBuilder();
		List<String> gbs = new ArrayList<>();
		for (int i = 0; i < gpSize; i++) {
			Field fd = groupList.get(i);
			collist.add(i, fd.getName());
			gbs.add(fd.getName());
			groupByKey.append(fd.getObjectname());
		}
		orderby.append(StringUtils.join(gbs, ","));
		groupBy.append(StringUtils.join(gbs, ","));
		String cols = StringUtils.join(collist, ",");
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("myBatis.select.groupby.xml.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("FIELD_SELECT_LIST", cols)
						.addMapEntry("ORDER_BY_FIELD", orderby.toString())
						.addMapEntry("GROUP_BY_KEY", groupByKey.toString())
						.addMapEntry("GROUP_BY_FIELD", groupBy.toString()).addMapEntry("TABLE_NAME", entity.getName())
						.addMapEntry("ENTITY_VAR_NAME", javaEntityVarName).addMapEntry("ENTITY_NAME", javaEntityName)
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		return generated;
	}

	public String genDrilldown() throws IOException {
		List<Field> sortFields = new ArrayList<>(this.allFields);
		Collections.sort(sortFields, new FieldOrderComparator());
		List<String> collist = new ArrayList<>();
		List<String> plainlist = new ArrayList<>();
		List<Field> groupList = entity.getDrillDownFields();
		if (groupList.size() > 0) {
			Collections.sort(groupList, new FieldDrillDownComparator());
		}
		Field gffd = entity.getGroupFunctionField();
		if (gffd != null) {
			collist.add(String.format("%1$s(%2$s) as %2$s", gffd.getGroupFunction().toString(), gffd.getName()));
			plainlist.add(gffd.getName());
		}
		int gpSize = groupList.size();
		String generated = null;
		StringBuilder allsql = new StringBuilder();
		if (gpSize > 0) {
			StringBuilder cols = new StringBuilder();
			List<String> gbs = new ArrayList<>();
			Field fd = groupList.get(0);
			List<String> acollist = new ArrayList<>(collist);
			acollist.add(0, fd.getName());
			gbs.add(fd.getName());
			cols.append(StringUtils.join(acollist, ','));
			generated = toGroupBySql("myBatis.select.drilldowntop.xml.tmpl", cols.toString(), fd, null, null);
			allsql.append(generated);
		}
		if (gpSize > 1) {
			List<String> innercollist = new ArrayList<>(plainlist);
			innercollist.add(0, groupList.get(0).getName());
			for (int i = 1; i < gpSize; i++) {
				List<String> acollist = new ArrayList<>(collist);
				List<String> whereClauses = new ArrayList<>();
				for (int k = 0; k < i; k++) {
					Field whfd = groupList.get(k);
					whereClauses.add(String.format(String.format("%s=#{%s,jdbcType=%s}", whfd.getName(),
							whfd.getVarname(), JdbcSqlTypeMap.getJdbcTyepName(whfd.getJdbcType()))));
				}
				innercollist.add(i, groupList.get(i).getName());
				acollist.add(0, groupList.get(i).getName());
				generated = toGroupBySql("myBatis.select.drilldown.xml.tmpl", StringUtils.join(acollist, ','),
						groupList.get(i), StringUtils.join(innercollist, ','), StringUtils.join(whereClauses, " AND "));
				allsql.append("\n").append(generated);
			}
		}
		List<String> selectList = new ArrayList<>();
		List<String> whereClauses = new ArrayList<>();
		List<String> orderby = new ArrayList<>();
		for (Field fd : sortFields) {
			selectList.add(fd.getName());
		}
		for (Field fd : groupList) {
			whereClauses.add(String.format(String.format("%s=#{%s,jdbcType=%s}", fd.getName(), fd.getVarname(),
					JdbcSqlTypeMap.getJdbcTyepName(fd.getJdbcType()))));
			orderby.add(fd.getName());
		}

		generated = TemplateUtil
				.renderTemplate(
						IOUtils.toString(
								getClass()
										.getResourceAsStream(
												"myBatis.select.drilldowndetail.xml.tmpl")),
						ParamMapBuilder.newBuilder()
								.addMapEntry("DETAIL_SELECT_LIST", StringUtils.join(selectList, ","))
								.addMapEntry("ORDER_BY_FIELD", StringUtils.join(orderby, ","))
								.addMapEntry("WHERE_CLAUSE", StringUtils.join(whereClauses, " AND "))
								.addMapEntry("TABLE_NAME", entity.getName())
								.addMapEntry("ENTITY_VAR_NAME", javaEntityVarName)
								.addMapEntry("ENTITY_NAME", javaEntityName)
								.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		allsql.append(generated);
		// System.out.println(generated);
		return allsql.toString();
	}

	protected String toGroupBySql(String tmpl, String selectionList, Field gbField, String innerSQl, String whereClause)
			throws IOException {
		return TemplateUtil.renderTemplate(IOUtils.toString(getClass().getResourceAsStream(tmpl)),
				ParamMapBuilder.newBuilder().addMapEntry("DRILLDOWN_SELECT_LIST", selectionList)
						.addMapEntry("ORDER_BY_FIELD", gbField.getName())
						.addMapEntry("GROUP_BY_KEY", gbField.getObjectname())
						.addMapEntry("GROUP_BY_FIELD", gbField.getName()).addMapEntry("WHERE_KEY_FIELD", whereClause)
						.addMapEntry("INNER_SELECT_LIST", innerSQl).addMapEntry("TABLE_NAME", entity.getName())
						.addMapEntry("ENTITY_VAR_NAME", javaEntityVarName).addMapEntry("ENTITY_NAME", javaEntityName)
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
	}

	public String genResultMap() throws IOException {
		StringBuilder fdmapping = new StringBuilder();
		String primaryKey = "";
		for (int i = 0; i < fieldsSize; i++) {
			Field fd = this.allFields.get(i);
			if (!fd.isPrimaryKey()) {
				fdmapping.append(String.format("<result property=\"%s\" column=\"%s\" />\n%s", fd.getVarname(),
						fd.getName(), INDENDENT_4));
			} else {
				primaryKey = String.format("<id property=\"%s\" column=\"%s\" />", fd.getVarname(), fd.getName());
			}
		}
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("myBatis.resultmap.xml.templ")),
				ParamMapBuilder.newBuilder().addMapEntry("PRIMARY_KEY_PROP", primaryKey)
						.addMapEntry("FIELD_MAPPINGS", fdmapping.toString()).addMapEntry("TABLE_NAME", entity.getName())
						.addMapEntry("ENTITY_VAR_NAME", javaEntityVarName).addMapEntry("ENTITY_NAME", javaEntityName)
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		return generated;
	}

	public MyBatisXmlEntityGenerator(CodeGenConfig configuration, Entity entity) {
		super(configuration, entity);
		this.javaEntityName = this.entity.getEntityObjectName();
		this.javaEntityVarName = this.entity.getEntityVarName();
		this.allFields = this.entity.getFields();
		this.fieldsSize = this.allFields.size();
		for (int i = 0; i < fieldsSize; i++) {
			Field fd = this.allFields.get(i);
			if (fd.isUnique()) {
				this.primaryKeys.add(fd);
			}
			this.allFieldDbNames.add(fd.getName());
			this.allFieldNames.add(fd.getObjectname());
		}
	}

}
