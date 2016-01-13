package com.asksunny.codegen.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.codegen.GroupFunction;
import com.asksunny.codegen.utils.ParamMapBuilder;
import com.asksunny.codegen.utils.TemplateUtil;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.FieldGroupLevelComparator;
import com.asksunny.schema.FieldOrderComparator;
import com.asksunny.schema.parser.JdbcSqlTypeMap;

public class MyBatisXmlEntityGenerator {

	static final String INDENDENT_1 = "    ";
	static final String INDENDENT_2 = "        ";
	static final String INDENDENT_3 = "            ";
	static final String INDENDENT_4 = "                ";

	private CodeGenConfig configuration;
	private Entity entity;
	private String javaEntityName = null;
	private String javaEntityVarName = null;
	private List<Field> primaryKeys = new ArrayList<>();
	private List<Field> allFields = null;
	private List<String> allFieldNames = new ArrayList<>();
	private List<String> allFieldDbNames = new ArrayList<>();
	private int fieldsSize = 0;

	public void doCodeGen() throws IOException {

	}

	public String genInsert() throws IOException {
		List<String> collist = new ArrayList<>();
		List<String> vallist = new ArrayList<>();
		for (int i = 0; i < fieldsSize; i++) {
			Field fd = this.allFields.get(i);
			if (!fd.isAutogen()) {
				collist.add(fd.getName());
				vallist.add(String.format("#{%s,jdbcType=%s}", fd.getVarname(),
						JdbcSqlTypeMap.getJdbcTyepName(fd.getJdbcType())));
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
		List<Field> sortFields = new ArrayList<>(this.allFields);
		Collections.sort(sortFields, new FieldOrderComparator());
		List<String> collist = new ArrayList<>();
		List<Field> keyFields = new ArrayList<>();

		for (int i = 0; i < fieldsSize; i++) {
			Field fd = sortFields.get(i);
			collist.add(fd.getName());
			if (fd.isUnique()) {
				keyFields.add(fd);
			}
		}
		String keyType = keyFields.size() > 1 ? javaEntityName : JdbcSqlTypeMap.toJavaTypeName(keyFields.get(0));
		List<String> whereList = new ArrayList<>();
		for (Field fd : keyFields) {
			whereList.add(String.format("%s=#{%s,jdbcType=%s}", fd.getName(), fd.getVarname(),
					JdbcSqlTypeMap.getJdbcTyepName(fd.getJdbcType())));
		}
		String whereClause = StringUtils.join(whereList, " AND\n");
		String orderby = entity.getOrderBy() == null ? "" : ("ORDER BY " + entity.getOrderBy());
		String cols = StringUtils.join(collist, ",");
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("myBatis.select.by.xml.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("FIELD_SELECT_LIST", cols).addMapEntry("KEY_TYPE", keyType)
						.addMapEntry("WHERE_KEY_FIELD", whereClause).addMapEntry("ORDER_BY", orderby)
						.addMapEntry("TABLE_NAME", entity.getName()).addMapEntry("ENTITY_VAR_NAME", javaEntityVarName)
						.addMapEntry("ENTITY_NAME", javaEntityName).addMapEntry("ENTITY_LABEL", entity.getLabel())
						.buildMap());
		return generated;
	}

	public String genSelectByGroup() throws IOException {
		List<Field> sortFields = new ArrayList<>(this.allFields);
		Collections.sort(sortFields, new FieldOrderComparator());
		List<String> collist = new ArrayList<>();
		List<Field> groupList = entity.getGroupByFields();
		for (int i = 0; i < fieldsSize; i++) {
			Field fd = sortFields.get(i);
			switch (fd.getGroupFunction()) {
			case NONE:
				break;
			case AVG:
				collist.add(String.format("%1$s(%2$s) as %2$s", "avg", fd.getName()));
				break;
			case COUNT:
				collist.add(String.format("%1$s(%2$s) as %2$s", "count", fd.getName()));
				break;
			case MAX:
				collist.add(String.format("%1$s(%2$s) as %2$s", "max", fd.getName()));
				break;
			case MEDIAN:
				collist.add(String.format("%1$s(%2$s) as %2$s", "median", fd.getName()));
				break;
			case MIN:
				collist.add(String.format("%1$s(%2$s) as %2$s", "min", fd.getName()));
				break;
			case STDDEV:
				collist.add(String.format("%1$s(%2$s) as %2$s", "stddev", fd.getName()));
				break;
			case SUM:
				collist.add(String.format("%1$s(%2$s) as %2$s", "sum", fd.getName()));
				break;
			}
		}
		if (groupList.size() > 0) {
			Collections.sort(groupList, new FieldGroupLevelComparator());
		}

		int gpSize = groupList.size();
		StringBuilder orderby = new StringBuilder();		
		StringBuilder groupBy = new StringBuilder();		
		List<String> gbs = new ArrayList<>();
		for (int i = 0; i < gpSize; i++) {
			Field fd = groupList.get(i);
			collist.add(i, fd.getName());
			gbs.add(fd.getName());
		}
		orderby.append(StringUtils.join(gbs, ","));
		groupBy.append(StringUtils.join(gbs, ","));
		String cols = StringUtils.join(collist, ",");
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream("myBatis.select.groupby.xml.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("FIELD_SELECT_LIST", cols)
						.addMapEntry("ORDER_BY_FIELD", orderby.toString())
						.addMapEntry("GROUP_BY_FIELD", groupBy.toString()).addMapEntry("TABLE_NAME", entity.getName())
						.addMapEntry("ENTITY_VAR_NAME", javaEntityVarName).addMapEntry("ENTITY_NAME", javaEntityName)
						.addMapEntry("ENTITY_LABEL", entity.getLabel()).buildMap());
		return generated;
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

	public CodeGenConfig getConfiguration() {
		return configuration;
	}

	public void setConfiguration(CodeGenConfig configuration) {
		this.configuration = configuration;
	}

	public MyBatisXmlEntityGenerator(CodeGenConfig configuration, Entity entity) {
		super();
		this.configuration = configuration;
		this.entity = entity;
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
