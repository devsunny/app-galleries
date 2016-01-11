package com.asksunny.codegen.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.codegen.CodeGenConfig.CodeOverwriteStrategy;
import com.asksunny.codegen.utils.JavaIdentifierUtil;
import com.asksunny.codegen.utils.SearchReplaceUtils;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.parser.JdbcSqlTypeMap;

public class EntityCodeGen {

	static final String INDENDENT_1 = "    ";
	static final String INDENDENT_2 = "        ";
	static final String INDENDENT_3 = "            ";
	static final String INDENDENT_4 = "                ";
	private String javaEntityName = null;
	private String javaEntityVarName = null;
	private List<Field> primaryKeys = new ArrayList<>();
	private List<Field> allFields = null;
	private List<String> allFieldNames = null;
	private List<String> allFieldDbNames = null;
	private String selectList = null;
	private CodeGenConfig config;
	private int fieldsSize = 0;
	private Entity entity;

	public EntityCodeGen(CodeGenConfig config, Entity entity) {
		this.config = config;
		this.entity = entity;
		this.javaEntityName = JavaIdentifierUtil.toObjectName(entity.getName());
		this.javaEntityVarName = JavaIdentifierUtil.toVariableName(entity.getName());
		this.allFields = entity.getFields();
		allFieldNames = new ArrayList<>();
		allFieldDbNames = new ArrayList<>();
		for (Field field : allFields) {
			if (field.isPrimaryKey()) {
				primaryKeys.add(field);
			}
			allFieldDbNames.add(field.getName());
			String name = field.getVarname() != null ? field.getVarname() : field.getName();
			allFieldNames.add(JavaIdentifierUtil.toVariableName(name));
		}
		this.selectList = StringUtils.join(this.allFieldDbNames, ',');
		fieldsSize = this.allFieldDbNames.size();
	}

	public void genCode() throws IOException {

		if (config.isGenDomainObject()) {
			writeCode(new File(config.getJavaBaseDir()), config.getDomainPackageName(), "", "java",
					toJavaDomainObject());
		}

		if (config.isGenMyBatisMapper()) {
			writeCode(new File(config.getJavaBaseDir()), config.getMapperPackageName(), "Mapper", "java",
					toMyBatisJavaMapper());
			writeCode(new File(config.getMyBatisXmlBaseDir()), config.getMapperPackageName(), "Mapper", "xml",
					toMyBatisXmlMapper());
		}

		if (config.isGenRestController()) {
			writeCode(new File(config.getJavaBaseDir()), config.getRestPackageName(), "RestController", "java",
					toRestController());
		}

	}

	public void writeCode(File dir, String pkName, String suffix, String ext, String code) throws IOException {
		String pkPath = pkName.replaceAll("\\.", "/");
		File f = new File(dir, pkPath);
		if (!f.exists() && !f.mkdirs()) {
			throw new IOException("Unable to wtite to directory:" + f.toString());
		}
		String name = String.format("%s%s.%s", javaEntityName, suffix, ext);
		File fj = new File(f, name);
		if (config.getOverwriteStrategy() == CodeOverwriteStrategy.IGNORE && fj.exists()) {
			return;
		} else if (config.getOverwriteStrategy() == CodeOverwriteStrategy.SUFFIX_SEQUENCE && fj.exists()) {
			for (int i = 1; i < Integer.MAX_VALUE; i++) {
				fj = new File(f, String.format("%s.%03d", name, i));
				if (!fj.exists()) {
					break;
				}
			}
		}
		FileWriter fw = new FileWriter(fj);
		try {
			fw.write(code);
			fw.flush();
		} finally {
			fw.close();
		}
	}

	public String toMyBatisJavaMapper() throws IOException {
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);

		if (StringUtils.isNotBlank(config.getMapperPackageName())) {
			out.printf("package %s;\n\n\n", config.getMapperPackageName());
		}
		out.printf("import %s.%s;\n\n", config.getDomainPackageName(), javaEntityName);

		out.printf("public interface %sMapper{\n", javaEntityName);
		out.printf("%1$svoid insert%2$s(%2$s %3$s);\n", INDENDENT_1, javaEntityName, javaEntityVarName);
		out.printf("%1$sjava.util.List<%2$s> get%2$s();\n", INDENDENT_1, javaEntityName);
		if (this.primaryKeys.size() == 1) {
			Field primaryKey = this.primaryKeys.get(0);
			String pname = primaryKey.getVarname() != null ? primaryKey.getVarname() : primaryKey.getName();
			String javaPKName = JavaIdentifierUtil.toObjectName(pname);
			String javaPKVarName = JavaIdentifierUtil.toVariableName(pname);
			String keyJavaTypeNmae = JdbcSqlTypeMap.toJavaTypeName(primaryKey);
			out.printf("%1$s%2$s get%2$sBy%3$s(%4$s %5$s);\n", INDENDENT_1, javaEntityName, javaPKName, keyJavaTypeNmae,
					javaPKVarName);
			out.printf("%1$svoid update%2$sBy%3$s(%2$s %6$s);\n", INDENDENT_1, javaEntityName, javaPKName,
					keyJavaTypeNmae, javaPKVarName, javaEntityVarName);
			out.printf("%1$svoid delete%2$sBy%3$s(%4$s %5$s);\n", INDENDENT_1, javaEntityName, javaPKName,
					keyJavaTypeNmae, javaPKVarName);
		} else if (this.primaryKeys.size() > 1) {
			out.printf("%1$s%2$s get%2$sByKey(%2$s %3$s);\n", INDENDENT_1, javaEntityName, javaEntityVarName);
			out.printf("%1$svoid update%2$sByKey(%2$s %3$s);\n", INDENDENT_1, javaEntityName, javaEntityVarName);
			out.printf("%1$svoid delete%2$sByKey(%2$s %3$s);\n", INDENDENT_1, javaEntityName, javaEntityVarName);
		}
		out.printf("}\n");
		String javaMapper = buf.toString();
		return javaMapper;
	}

	public String toMyBatisXmlMapper() throws IOException {
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);

		out.printf("%s<resultMap id=\"%sResultMap\" type=\"%s\">\n", INDENDENT_1, javaEntityVarName, javaEntityName);
		for (int i = 0; i < fieldsSize; i++) {
			out.printf("%1$s<result property=\"%2$s\" column=\"%3$s\" />\n", INDENDENT_2, allFieldNames.get(i),
					allFieldDbNames.get(i));
		}
		out.printf("%s</resultMap>\n\n", INDENDENT_1);
		// ----Do insert Here ----
		out.printf("%s<insert id=\"insert%2$s\" parameterType=\"%2$s\">\n", INDENDENT_1, javaEntityName);
		out.printf("%2$sINSERT INTO %3$s \n%1$s%2$s(%4$s)\n", INDENDENT_1, INDENDENT_2, entity.getName(), selectList);
		out.printf("%1$sVALUES (\n", INDENDENT_2);
		for (int i = 0; i < fieldsSize; i++) {
			Field field = allFields.get(i);
			out.printf("%s#{%s,jdbcType=%s}", INDENDENT_3, allFieldNames.get(i),
					JdbcSqlTypeMap.getJdbcTyepName(field.getJdbcType()));
			if (i < fieldsSize - 1) {
				out.println(",");
			} else {
				out.println();
			}
		}
		out.printf("%s)\n", INDENDENT_2);
		out.printf("%s</insert>\n", INDENDENT_1);

		// --- do Select ALL here
		out.printf("%3$s<select id=\"get%s\" resultMap=\"%sResultMap\">\n", javaEntityName, javaEntityVarName,
				INDENDENT_1);
		out.println(INDENDENT_2 + "SELECT");
		out.println(INDENDENT_3 + selectList);
		out.println(INDENDENT_2 + "FROM");
		out.println(INDENDENT_3 + entity.getName());
		out.println(INDENDENT_1 + "</select>");
		if (this.primaryKeys.size() == 1) {
			Field primaryKey = this.primaryKeys.get(0);
			String pname = primaryKey.getVarname() != null ? primaryKey.getVarname() : primaryKey.getName();
			String javaPKName = JavaIdentifierUtil.toObjectName(pname);
			String javaPKVarName = JavaIdentifierUtil.toVariableName(pname);
			String keyJavaTypeNmae = JdbcSqlTypeMap.toJavaTypeName(primaryKey);
			out.printf("%1$s<select id=\"get%2$sBy%5$s\" resultMap=\"%3$sResultMap\" parameterType=\"%4$s\">\n",
					INDENDENT_1, javaEntityName, javaEntityVarName, keyJavaTypeNmae, javaPKName);
			out.println(INDENDENT_2 + "SELECT");
			out.println(INDENDENT_3 + selectList);
			out.println(INDENDENT_2 + "FROM");
			out.println(INDENDENT_3 + entity.getName());
			out.printf("%1$sWHERE  %2$s=#{%3$s,jdbcType=%4$s}\n", INDENDENT_2, primaryKey.getName(), javaPKVarName,
					JdbcSqlTypeMap.getJdbcTyepName(primaryKey.getJdbcType()));
			out.println(INDENDENT_1 + "</select>");

			out.printf("%1$s<update id=\"update%2$sBy%5$s\" parameterType=\"%2$s\">\n", INDENDENT_1, javaEntityName,
					javaEntityVarName, keyJavaTypeNmae, javaPKName);
			out.printf("%1$sUPDATE  %2$s  SET\n", INDENDENT_2, javaEntityName);
			printUpdateList(out);
			out.printf("\n%sWHERE\n", INDENDENT_2);
			printWhereClause(out);
			out.printf("%1$s</update>\n", INDENDENT_1);

			out.printf("%1$s<delete id=\"delete%2$sBy%5$s\" parameterType=\"%4$s\">\n", INDENDENT_1, javaEntityName,
					javaEntityVarName, keyJavaTypeNmae, javaPKName);
			out.printf("%1$sDELETE FROM %2$s\n", INDENDENT_2, javaEntityName);
			out.printf("%sWHERE\n", INDENDENT_2);
			printWhereClause(out);
			out.printf("%1$s</delete>\n", INDENDENT_1);

		} else if (this.primaryKeys.size() > 1) {
			out.printf("%1$s<select id=\"get%2$sByKey\" resultMap=\"%3$sResultMap\" parameterType=\"%2$s\">\n",
					INDENDENT_1, javaEntityName, javaEntityVarName);
			out.println(INDENDENT_2 + "SELECT");
			out.println(INDENDENT_3 + selectList);
			out.println(INDENDENT_2 + "FROM");
			out.println(INDENDENT_3 + entity.getName());
			out.printf("%1$sWHERE \n", INDENDENT_2);
			printWhereClause(out);
			out.println(INDENDENT_1 + "</select>");

			out.printf("%1$s<update id=\"update%2$sByKey\" parameterType=\"%2$s\">\n", INDENDENT_1, javaEntityName);
			out.printf("%1$sUPDATE  %2$s  SET\n", INDENDENT_2, javaEntityName);
			printUpdateList(out);
			out.printf("\n%sWHERE\n", INDENDENT_2);
			printWhereClause(out);
			out.printf("%1$s</update>\n", INDENDENT_1);

			out.printf("%1$s<delete id=\"delete%2$sByKey\" parameterType=\"%2$s\">\n", INDENDENT_1, javaEntityName);
			out.printf("%1$sDELETE FROM %2$s\n", INDENDENT_2, javaEntityName);
			out.printf("%sWHERE\n", INDENDENT_2);
			printWhereClause(out);
			out.printf("%1$s</delete>\n", INDENDENT_1);

		}

		String text = IOUtils.toString(getClass().getResourceAsStream("MyBatis.tmpl"));
		Map<String, String> params = new HashMap<String, String>();
		params.put("SQLMAP", buf.toString());
		params.put("namepace", String.format("%s.%s", this.config.getMapperPackageName(), javaEntityName));
		String sqlMap = SearchReplaceUtils.searchAndReplace(text, params);
		// System.out.println(sqlMap);
		return sqlMap;
	}

	protected void printUpdateList(PrintWriter out) {
		int i = 0;
		for (int j = 0; j < fieldsSize; j++) {
			Field field = allFields.get(j);
			if (field.isPrimaryKey()) {
				continue;
			}
			if (i > 0) {
				out.println(",");
			}
			out.printf("%1$s%2$s=#{%3$s,jdbcType=%4$s}", INDENDENT_3, field.getName(), this.allFieldNames.get(j),
					JdbcSqlTypeMap.getJdbcTyepName(field.getJdbcType()));
			i++;
		}
	}

	protected void printWhereClause(PrintWriter out) {
		int i = 0;
		for (Field primaryKey : this.primaryKeys) {
			if (i > 0) {
				out.println(INDENDENT_3 + "AND");
			}
			String pname = primaryKey.getVarname() != null ? primaryKey.getVarname() : primaryKey.getName();
			out.printf("%1$s%2$s=#{%3$s,jdbcType=%4$s}\n", INDENDENT_3, primaryKey.getName(),
					JavaIdentifierUtil.toVariableName(pname), JdbcSqlTypeMap.getJdbcTyepName(primaryKey.getJdbcType()));
			i++;
		}
	}

	public String toRestController() throws IOException {
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);

		out.printf("%1$s@RequestMapping(method = { RequestMethod.PUT })\n", INDENDENT_1);
		out.printf("%1$s@ResponseBody\n", INDENDENT_1);
		out.printf("%1$spublic %2$s add%2$s(@RequestBody %2$s %3$s){\n", INDENDENT_1, javaEntityName,
				javaEntityVarName);
		out.printf("%1$sthis.%3$sMapper.insert%2$s(%3$s);\n", INDENDENT_2, javaEntityName, javaEntityVarName);
		out.printf("%1$sreturn %3$s;\n", INDENDENT_2, javaEntityName, javaEntityVarName);
		out.printf("%1$s}\n\n", INDENDENT_1);

		out.printf("%1$s@RequestMapping(method = { RequestMethod.GET })\n", INDENDENT_1);
		out.printf("%1$s@ResponseBody\n", INDENDENT_1);
		out.printf("%1$spublic java.util.List<%2$s> get%2$s(){\n", INDENDENT_1, javaEntityName, javaEntityVarName);
		out.printf("%1$sjava.util.List<%2$s> ret = this.%3$sMapper.get%2$s();\n", INDENDENT_2, javaEntityName,
				javaEntityVarName);
		out.printf("%1$sreturn ret;\n", INDENDENT_2);
		out.printf("%1$s}\n\n", INDENDENT_1);

		if (this.primaryKeys.size() == 1) {
			Field primaryKey = this.primaryKeys.get(0);
			String pname = primaryKey.getVarname() != null ? primaryKey.getVarname() : primaryKey.getName();
			String javaPKName = JavaIdentifierUtil.toObjectName(pname);
			String javaPKVarName = JavaIdentifierUtil.toVariableName(pname);
			String keyJavaTypeName = JdbcSqlTypeMap.toJavaTypeName(primaryKey);

			out.printf("%1$s@RequestMapping(value = \"/{%2$s}\", method = { RequestMethod.GET })\n", INDENDENT_1,
					javaPKVarName);
			out.printf("%1$s@ResponseBody\n", INDENDENT_1);
			out.printf("%5$spublic %1$s get%1$sBy%2$s(@PathVariable(\"%4$s\") %3$s %4$s){\n", javaEntityName,
					javaPKName, keyJavaTypeName, javaPKVarName, INDENDENT_1);
			out.printf("%5$s%1$s  ret = this.%2$sMapper.get%1$sBy%3$s(%4$s);\n", javaEntityName, javaEntityVarName,
					javaPKName, javaPKVarName, INDENDENT_2);
			out.printf("%3$sreturn ret;\n", javaEntityName, javaEntityVarName, INDENDENT_2);
			out.printf("%1$s}\n\n", INDENDENT_1);

			out.printf("%1$s@RequestMapping(method = { RequestMethod.POST })\n", INDENDENT_1);
			out.printf("%1$s@ResponseBody\n", INDENDENT_1);
			out.printf("%4$spublic %1$s update%1$sBy%2$s(@RequestBody %1$s %3$s){\n", javaEntityName, javaPKName,
					javaEntityVarName, INDENDENT_1);
			out.printf("%4$sthis.%2$sMapper.update%1$sBy%3$s(%2$s);\n", javaEntityName, javaEntityVarName, javaPKName,
					INDENDENT_2);
			out.printf("%1$sreturn %2$s;\n", INDENDENT_2, javaEntityVarName);
			out.printf("%1$s}\n\n", INDENDENT_1);

			out.printf("%1$s@RequestMapping(value = \"/{%2$s}\", method = { RequestMethod.DELETE })\n", INDENDENT_1,
					javaPKVarName);
			out.printf("%1$s@ResponseBody\n", INDENDENT_1);
			out.printf("%5$spublic void delete%1$sBy%2$s(@PathVariable(\"%4$s\") %3$s %4$s){\n", javaEntityName,
					javaPKName, keyJavaTypeName, javaPKVarName, INDENDENT_1);
			out.printf("%5$sthis.%2$sMapper.delete%1$sBy%3$s(%4$s);\n", javaEntityName, javaEntityVarName, javaPKName,
					javaPKVarName, INDENDENT_2);
			out.printf("%1$s}\n\n", INDENDENT_1);

		} else if (this.primaryKeys.size() > 1) {

			out.printf("%1$s@RequestMapping( method = { RequestMethod.GET })\n", INDENDENT_1);
			out.printf("%1$s@ResponseBody\n", INDENDENT_1);
			out.printf("%3$spublic %1$s get%1$sByKey(@RequestBody %1$s %2$s){\n", javaEntityName, javaEntityVarName,
					INDENDENT_1);
			out.printf("%3$s%1$s ret = this.%2$sMapper.get%1$sByKey(%2$s);\n", javaEntityName, javaEntityVarName,
					INDENDENT_2);
			out.printf("%1$sreturn ret;\n", INDENDENT_2);
			out.printf("%1$s}\n\n", INDENDENT_1);

			out.printf("%1$s@RequestMapping(method = { RequestMethod.POST })\n", INDENDENT_1);
			out.printf("%1$s@ResponseBody\n", INDENDENT_1);
			out.printf("%3$spublic %1$s update%1$sByKey(@RequestBody %1$s %2$s){\n", javaEntityName, javaEntityVarName,
					INDENDENT_1);
			out.printf("%3$sthis.%2$sMapper.update%1$sByKey(%2$s);\n", javaEntityName, javaEntityVarName, INDENDENT_2);
			out.printf("%1$sreturn %2$s;\n", INDENDENT_2, javaEntityVarName);
			out.printf("%1$s}\n\n", INDENDENT_1);

			out.printf("%1$s@RequestMapping(method = { RequestMethod.DELETE })\n", INDENDENT_1);
			out.printf("%1$s@ResponseBody\n", INDENDENT_1);
			out.printf("%3$spublic %1$s delete%1$sByKey(@RequestBody %1$s %2$s){\n", javaEntityName, javaEntityVarName,
					INDENDENT_1);
			out.printf("%3$sthis.%2$sMapper.delete%1$sByKey(%2$s);\n", javaEntityName, javaEntityVarName, INDENDENT_2);
			out.printf("%1$sreturn %2$s;\n", INDENDENT_2, javaEntityVarName);
			out.printf("%1$s}\n\n", INDENDENT_1);

		}

		String text = IOUtils.toString(getClass().getResourceAsStream("RestController.tmpl"));
		Map<String, String> params = new HashMap<String, String>();
		params.put("packageName", config.getRestPackageName());
		params.put("javaEntityName", javaEntityName);
		params.put("javaEntityVarName", javaEntityVarName);
		params.put("JAVA_MAPPER_IMPLS", buf.toString());
		params.put("IMPORT", String.format("import %1$s.%3$s;\nimport %2$s.%3$sMapper;\n\n",
				config.getDomainPackageName(), config.getMapperPackageName(), javaEntityName));

		String cntrl = SearchReplaceUtils.searchAndReplace(text, params);
		// System.out.println(cntrl);
		return cntrl;
	}

	public String toJavaDomainObject() throws IOException {
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);
		if (StringUtils.isNotBlank(config.getDomainPackageName())) {
			out.printf("package %s;\n\n\n", config.getDomainPackageName());
		}
		out.println("import java.io.Serializable;\n\n");
		out.printf("public class %s implements Serializable {\n\n", this.javaEntityName);
		out.printf("%sprivate static final long serialVersionUID = 1L;\n\n", INDENDENT_1);

		for (Field field : this.allFields) {
			String name = field.getVarname() != null ? field.getVarname() : field.getName();
			out.printf("%sprivate %s %s;\n\n", INDENDENT_1, JdbcSqlTypeMap.toJavaTypeName(field),
					JavaIdentifierUtil.toVariableName(name));

		}
		out.println("\n");
		for (int i = 0; i < fieldsSize; i++) {
			Field field = this.allFields.get(i);
			String name = allFieldNames.get(i);
			out.printf("%spublic %s get%s(){\n", INDENDENT_1, JdbcSqlTypeMap.toJavaTypeName(field),
					JavaIdentifierUtil.toObjectName(name));
			out.printf("%sreturn this.%s;\n", INDENDENT_2, JavaIdentifierUtil.toVariableName(name));
			out.printf("%s}\n\n", INDENDENT_1);
			out.printf("%s public void set%s(%s %s){\n", INDENDENT_1, JavaIdentifierUtil.toObjectName(name),
					JdbcSqlTypeMap.toJavaTypeName(field), JavaIdentifierUtil.toVariableName(name));
			out.printf("%1$sthis.%2$s = %2$s;\n", INDENDENT_2, JavaIdentifierUtil.toVariableName(name));
			out.printf("%s}\n\n", INDENDENT_1);
		}
		out.println("}");
		out.flush();
		return buf.toString();

	}

}
