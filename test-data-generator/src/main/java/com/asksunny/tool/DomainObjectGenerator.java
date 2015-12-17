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

public class DomainObjectGenerator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void generateDomainModel(String baseDir, String packageName, String schemaFile) throws Exception {
		SQLScriptParser parser = new SQLScriptParser(new File(schemaFile));
		Schema schema = parser.parseSql();
		List<Entity> entites = schema.getAllEntities();
		File dir = new File("src/main/java", packageName.replaceAll("\\.", "/"));
		for (Entity entity : entites) {
			String text = toJavaDomainObject(entity, packageName);
			FileWriter fw = new FileWriter(
					new File(dir, String.format("%s.java", JavaIdentifierUtil.toObjectName(entity.getName()))));
			try {
				fw.write(text);
				fw.flush();
			} finally {
				fw.close();
			}
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
		out.printf("public class %s implements Serializable {\n", JavaIdentifierUtil.toObjectName(entity.getName()));
		out.println(" ");
		out.println("     private static final long serialVersionUID = 1L;");
		out.println(" ");
		List<Field> fields = entity.getFields();
		for (Field field : fields) {
			String name = field.getObjname() != null ? field.getObjname() : field.getName();
			out.printf("     private %s %s;\n", JdbcSqlTypeMap.toJavaTypeName(field.getJdbcType()),
					JavaIdentifierUtil.toVariableName(name));
			out.println(" ");
		}
		out.println(" ");
		out.println(" ");
		for (Field field : fields) {
			String name = field.getObjname() != null ? field.getObjname() : field.getName();
			out.printf("     public %s get%s(){\n", JdbcSqlTypeMap.toJavaTypeName(field.getJdbcType()),
					JavaIdentifierUtil.toObjectName(name));
			out.printf("         return this.%s;\n", JavaIdentifierUtil.toVariableName(name));
			out.println("     }");

			out.printf("     public void set%s(%s %s){\n", JavaIdentifierUtil.toObjectName(name),
					JdbcSqlTypeMap.toJavaTypeName(field.getJdbcType()), JavaIdentifierUtil.toVariableName(name));
			out.printf("          this.%1$s = %1$s;\n", JavaIdentifierUtil.toVariableName(name));
			out.println("     }");
			out.println(" ");
		}

		out.println("}");
		out.flush();
		return buf.toString();

	}

}
