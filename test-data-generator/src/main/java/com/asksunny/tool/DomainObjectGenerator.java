package com.asksunny.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import org.junit.Test;

import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.Schema;
import com.asksunny.schema.parser.JdbcSqlTypeMap;
import com.asksunny.schema.parser.SQLScriptParser;

public class DomainObjectGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Test
	public void testNothing() throws Exception {
		InputStream in = getClass().getResourceAsStream("/schema.ddl.sql");
		InputStreamReader reader = new InputStreamReader(in);
		generateDomainModel("src/main/java", "com.chase.wm.domain", reader);
		reader.close();
	}

	public void generateDomainModel(String baseDir, String packageName,
			String schemaFile) throws Exception {
		generateDomainModel(baseDir, packageName, new FileReader(schemaFile));
	}

	public void generateDomainModel(String baseDir, String packageName,
			Reader schemaFileReader) throws Exception {
		SQLScriptParser parser = new SQLScriptParser(schemaFileReader);
		Schema schema = parser.parseSql();
		List<Entity> entites = schema.getAllEntities();
		File dir = new File(baseDir, packageName.replaceAll("\\.", "/"));
		if (!dir.exists()) {
			dir.mkdirs();
		}
		for (Entity entity : entites) {
			// toJavaDomainObjectSource(dir, entity, packageName);
			// toJavaDomainObjectMyBatisJavaMapper(entity, packageName);
			System.out.println(toJavaDomainObjectMyBatisMapper(entity, packageName));
			toSpringRestController(entity, packageName);
		}
	}

	protected void toJavaDomainObjectMyBatisSource(File dir, Entity entity,
			String packageName) throws IOException {
		String text = toJavaDomainObject(entity, packageName);
		FileWriter fw = new FileWriter(new File(dir, String.format("%s.java",
				JavaIdentifierUtil.toObjectName(entity.getName()))));
		try {
			fw.write(text);
			fw.flush();
		} finally {
			fw.close();
		}
	}

	protected String toSpringRestController(Entity entity, String packageName)
			throws IOException {
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);

		List<Field> fields = entity.getFields();
		List<String> fieldNames = new ArrayList<>();
		List<String> javaFieldName = new ArrayList<>();
		Field primaryKey = null;
		String javaEntityName = JavaIdentifierUtil.toObjectName(entity
				.getName());
		String javaEntityVarName = JavaIdentifierUtil.toVariableName(entity
				.getName());
		for (Field field : fields) {
			String name = field.getObjname() != null ? field.getObjname()
					: field.getName();
			javaFieldName.add(name);
			String fname = field.getName();
			fieldNames.add(fname);
			if (field.isPrimaryKey()) {
				primaryKey = field;
			}
		}
		

		out.println("    @RequestMapping(method = { RequestMethod.PUT })");
		out.println("    @ResponseBody");
		out.printf("    public %1$s add%1$s(@RequestBody %1$s %2$s){\n", javaEntityName,		javaEntityVarName);
		out.printf("        this.%2$sMapper.insert%1$s(%2$s);\n",		javaEntityName, javaEntityVarName);
		out.printf("        return %2$s;\n", javaEntityName, javaEntityVarName);
		out.println("    }");
		
		
		out.println("    @RequestMapping(method = { RequestMethod.GET })");
		out.println("    @ResponseBody");
		out.printf("    public java.util.List<%1$s> get%1$s(){\n", javaEntityName,		javaEntityVarName);
		out.printf("        java.util.List<%1$s> ret = this.%2$sMapper.get%1$s();\n",		javaEntityName, javaEntityVarName);
		out.printf("        return ret;\n", javaEntityName, javaEntityVarName);
		out.println("    }");
		
		
		if(primaryKey!=null){
			String pname = primaryKey.getObjname() != null ? primaryKey
					.getObjname() : primaryKey.getName();
			String javaPKName = JavaIdentifierUtil.toObjectName(pname);
			String javaPKVarName = JavaIdentifierUtil.toVariableName(pname);
			
			out.printf("    @RequestMapping(value = \"/{%s}\" method = { RequestMethod.GET })\n", javaPKVarName);
			out.println("   @ResponseBody");
			out.printf("    public %1$s get%1$sBy%2$s(@PathVariable(\"%4$s\") %3$s %4$s){\n", javaEntityName, javaPKName,  JdbcSqlTypeMap.toJavaTypeName(primaryKey.getJdbcType()),  javaPKVarName);
			out.printf("        %1$s ret = this.%2$sMapper.get%1$sBy%3$s(%4$s);\n",	javaEntityName, javaEntityVarName, javaPKName, javaPKVarName);
			out.printf("        return ret;\n", javaEntityName, javaEntityVarName);
			out.println("    }");
			
			out.printf("    @RequestMapping(method = { RequestMethod.POST })\n", javaPKVarName);
			out.println("   @ResponseBody");
			out.printf("    public %1$s update%1$sBy%2$s(@RequestBody %1$s %3$s){\n", javaEntityName, javaPKName,  javaEntityVarName);
			out.printf("        this.%2$sMapper.update%1$sBy%3$s(%2$s);\n",	javaEntityName, javaEntityVarName, javaPKName, javaPKVarName);
			out.printf("        return ret;\n", javaEntityName, javaEntityVarName);
			out.println("    }");
			
			
			out.printf("    @RequestMapping(value = \"/{%s}\" method = { RequestMethod.DELETE })\n", javaPKVarName);
			out.println("   @ResponseBody");
			out.printf("    public %3$s delete%1$sBy%2$s(@PathVariable(\"%4$s\") %3$s %4$s){\n", javaEntityName, javaPKName,  JdbcSqlTypeMap.toJavaTypeName(primaryKey.getJdbcType()),  javaPKVarName);
			out.printf("        this.%2$sMapper.delete%1$sBy%3$s(%4$s);\n",	javaEntityName, javaEntityVarName, javaPKName, javaPKVarName);
			out.printf("        return %2$s;\n", javaEntityName, javaEntityVarName);
			out.println("    }");
		}
		
		

		String text = IOUtils.toString(getClass().getResourceAsStream(
				"RestController.tmpl"));
		Map<String, String> params = new HashMap<String, String>();
		params.put("packageName", packageName);
		params.put("javaEntityName", javaEntityName);
		params.put("javaEntityVarName", javaEntityVarName);
		params.put("JAVA_MAPPER_IMPLS", buf.toString());

		String cntrl = SearchReplaceUtils.searchAndReplace(text, params);
		System.out.println(cntrl);
		return cntrl;
	}

	protected String toJavaDomainObjectMyBatisJavaMapper(Entity entity,
			String packageName) {
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);
		out.printf("package %s;\n", packageName);
		List<Field> fields = entity.getFields();
		List<String> fieldNames = new ArrayList<>();
		List<String> javaFieldName = new ArrayList<>();
		Field primaryKey = null;
		String javaEntityName = JavaIdentifierUtil.toObjectName(entity
				.getName());
		String javaEntityVarName = JavaIdentifierUtil.toVariableName(entity
				.getName());
		for (Field field : fields) {
			String name = field.getObjname() != null ? field.getObjname()
					: field.getName();
			javaFieldName.add(name);
			String fname = field.getName();
			fieldNames.add(fname);
			if (field.isPrimaryKey()) {
				primaryKey = field;
			}
		}
		out.println();
		out.println();
		out.printf("public interface %sMapper{\n", javaEntityName);

		out.printf(" void insert%s(%s %s);\n", javaEntityName, javaEntityName,
				javaEntityVarName);
		out.printf(" java.util.List<%s> get%s();\n", javaEntityName,
				javaEntityName, javaEntityVarName);
		if (primaryKey != null) {
			String pname = primaryKey.getObjname() != null ? primaryKey
					.getObjname() : primaryKey.getName();
			String javaPKName = JavaIdentifierUtil.toObjectName(pname);
			String javaPKVarName = JavaIdentifierUtil.toVariableName(pname);
			out.printf(" %s get%sBy%s(%s %s);\n", javaEntityName,
					javaEntityName, javaPKName,
					JdbcSqlTypeMap.toJavaTypeName(primaryKey.getJdbcType()),
					javaPKVarName);
			out.printf(" void update%sBy%s(%s %s);\n", javaEntityName,
					javaPKName,
					JdbcSqlTypeMap.toJavaTypeName(primaryKey.getJdbcType()),
					javaPKVarName);
			out.printf(" void delete%sBy%s(%s %s);\n", javaEntityName,
					javaPKName,
					JdbcSqlTypeMap.toJavaTypeName(primaryKey.getJdbcType()),
					javaPKVarName);

		}
		out.printf("}\n");

		String javaMapper = buf.toString();
		System.out.println(javaMapper);
		return javaMapper;
	}

	protected String toJavaDomainObjectMyBatisMapper(Entity entity,
			String packageName) throws IOException {
		String text = IOUtils.toString(getClass().getResourceAsStream(
				"MyBatis.tmpl"));
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);
		out.printf("<resultMap id=\"%sResultMap\" type=\"%s\">\n",
				JavaIdentifierUtil.toVariableName(entity.getName()),
				JavaIdentifierUtil.toObjectName(entity.getName()));
		List<Field> fields = entity.getFields();
		List<String> fieldNames = new ArrayList<>();
		Field primaryKey = null;
		for (Field field : fields) {
			String name = field.getObjname() != null ? field.getObjname()
					: field.getName();
			String fname = field.getName();
			fieldNames.add(fname);
			if (field.isPrimaryKey()) {
				primaryKey = field;
			}
			out.printf("    <result property=\"%s\" column=\"%s\" />	\n",
					JavaIdentifierUtil.toVariableName(name), fname);
		}
		out.println("</resultMap>");
		String selectList = StringUtils.join(fieldNames, ',');

		out.printf("<insert id=\"insert%s\" parameterType=\"%s\">\n",
				JavaIdentifierUtil.toObjectName(entity.getName()),
				JavaIdentifierUtil.toObjectName(entity.getName()));
		out.printf("    INSERT INTO %s \n    (%s)\n", entity.getName(),
				selectList);
		out.println("    VALUES (");
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			String name = field.getObjname() != null ? field.getObjname()
					: field.getName();
			String fname = field.getName();
			fieldNames.add(fname);
			out.printf("    %s=#{%s,jdbcType=%s}", fname, JavaIdentifierUtil
					.toVariableName(name), JdbcSqlTypeMap.getInstance()
					.findJdbcTypeName(field.getJdbcType()));
			if (i < fields.size() - 1) {
				out.println(",");
			} else {
				out.println();
			}
		}
		out.println("    )");
		out.println("</insert>");

		out.printf("<select id=\"get%s\" resultMap=\"%sResultMap\">\n",
				JavaIdentifierUtil.toObjectName(entity.getName()),
				JavaIdentifierUtil.toVariableName(entity.getName()));
		out.println("    SELECT");
		out.println("     " + selectList);
		out.println("    FROM");
		out.println("     " + entity.getName());
		out.println("</select>");

		if (primaryKey != null) {
			String name = primaryKey.getObjname() != null ? primaryKey
					.getObjname() : primaryKey.getName();
			String fname = primaryKey.getName();
			out.printf(
					"<select id=\"get%sBy%s\" parameterType=\"%s\" resultMap=\"%sResultMap\">\n",
					JavaIdentifierUtil.toObjectName(entity.getName()),
					JavaIdentifierUtil.toObjectName(name),
					JdbcSqlTypeMap.toJavaTypeName(primaryKey.getJdbcType()),
					JavaIdentifierUtil.toVariableName(entity.getName()));
			out.println("    SELECT");
			out.println("     " + selectList);
			out.println("    FROM");
			out.println("     " + entity.getName());
			out.println(String.format(
					"    WHERE  %s=#{%s,jdbcType=%s}",
					fname,
					JavaIdentifierUtil.toVariableName(name),
					JdbcSqlTypeMap.getInstance().findJdbcTypeName(
							primaryKey.getJdbcType())));
			out.println("</select>");

			out.printf("<update id=\"update%sBy%s\" parameterType=\"%s\">\n",
					JavaIdentifierUtil.toObjectName(entity.getName()),
					JavaIdentifierUtil.toObjectName(name),
					JavaIdentifierUtil.toObjectName(entity.getName()));
			out.println(String.format("    UPDATE  %s  SET",
					JavaIdentifierUtil.toObjectName(entity.getName())));
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				if (field.isPrimaryKey())
					continue;
				String fxname = field.getObjname() != null ? field.getObjname()
						: field.getName();
				String fxfname = field.getName();
				fieldNames.add(fname);
				out.printf(
						"    %s=#{%s,jdbcType=%s}",
						fxfname,
						JavaIdentifierUtil.toVariableName(fxname),
						JdbcSqlTypeMap.getInstance().findJdbcTypeName(
								field.getJdbcType()));
				if (i < fields.size() - 1) {
					out.println(",");
				} else {
					out.println();
				}
			}

			out.printf(
					"     WHERE  %s=#{%s,jdbcType=%s}\n",
					fname,
					JavaIdentifierUtil.toVariableName(name),
					JdbcSqlTypeMap.getInstance().findJdbcTypeName(
							primaryKey.getJdbcType()));
			out.println("</update>");

			out.printf("<delete id=\"delete%sBy%s\" parameterType=\"%s\">\n",
					JavaIdentifierUtil.toObjectName(entity.getName()),
					JavaIdentifierUtil.toObjectName(name),
					JdbcSqlTypeMap.toJavaTypeName(primaryKey.getJdbcType()));
			out.println(String.format(
					"    DELETE FROM %s WHERE  %s=#{%s,jdbcType=%s}",
					JavaIdentifierUtil.toObjectName(entity.getName()),
					fname,
					JavaIdentifierUtil.toVariableName(name),
					JdbcSqlTypeMap.getInstance().findJdbcTypeName(
							primaryKey.getJdbcType())));
			out.println("</delete>");

		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("SQLMAP", buf.toString());
		params.put(
				"namepace",
				String.format("%s.%s", packageName,
						JavaIdentifierUtil.toObjectName(entity.getName())));
		String sqlMap = SearchReplaceUtils.searchAndReplace(text, params);

		// System.out.println(sqlMap);
		return sqlMap;
	}

	protected void toJavaDomainObjectSource(File dir, Entity entity,
			String packageName) throws IOException {
		String text = toJavaDomainObject(entity, packageName);
		FileWriter fw = new FileWriter(new File(dir, String.format("%s.java",
				JavaIdentifierUtil.toObjectName(entity.getName()))));
		try {
			fw.write(text);
			fw.flush();
		} finally {
			fw.close();
		}
	}

	public String toJavaDomainObject(Entity entity, String packageName) {
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);
		if (packageName != null) {
			out.printf("package %s;\n", packageName);
			out.println(" ");
		}
		out.println("import java.io.Serializable;");
		out.println(" ");
		out.println(" ");
		out.printf("public class %s implements Serializable {\n",
				JavaIdentifierUtil.toObjectName(entity.getName()));
		out.println(" ");
		out.println("     private static final long serialVersionUID = 1L;");
		out.println(" ");
		List<Field> fields = entity.getFields();
		for (Field field : fields) {
			String name = field.getObjname() != null ? field.getObjname()
					: field.getName();
			out.printf("     private %s %s;\n",
					JdbcSqlTypeMap.toJavaTypeName(field.getJdbcType()),
					JavaIdentifierUtil.toVariableName(name));
			out.println(" ");
		}
		out.println(" ");
		out.println(" ");
		for (Field field : fields) {
			String name = field.getObjname() != null ? field.getObjname()
					: field.getName();
			out.printf("     public %s get%s(){\n",
					JdbcSqlTypeMap.toJavaTypeName(field.getJdbcType()),
					JavaIdentifierUtil.toObjectName(name));
			out.printf("         return this.%s;\n",
					JavaIdentifierUtil.toVariableName(name));
			out.println("     }");

			out.printf("     public void set%s(%s %s){\n",
					JavaIdentifierUtil.toObjectName(name),
					JdbcSqlTypeMap.toJavaTypeName(field.getJdbcType()),
					JavaIdentifierUtil.toVariableName(name));
			out.printf("          this.%1$s = %1$s;\n",
					JavaIdentifierUtil.toVariableName(name));
			out.println("     }");
			out.println(" ");
		}

		out.println("}");
		out.flush();
		return buf.toString();

	}

}
