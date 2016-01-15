package com.asksunny.codegen.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.codegen.utils.ParamMapBuilder;
import com.asksunny.codegen.utils.SearchReplaceUtils;
import com.asksunny.codegen.utils.TemplateUtil;

public class SourceCodeUtil {

	public static void getSpringContext(CodeGenConfig config) throws IOException {
		writeSpringTemplate(config, "pom.xml.tmpl");
		writeSpringTemplate(config, "spring-jetty-booststrap.xml.tmpl");
		writeSpringTemplate(config, "spring-webapp-context.xml.tmpl");
		writeSpringTemplate(config, "spring-webui-context.xml.tmpl");
	}

	public static void writeSpringBootstrap(CodeGenConfig configuration) throws IOException {

		String bootstrappackage = configuration.getAppBootstrapPackage() == null ? ""
				: String.format("package %s;", configuration.getAppBootstrapPackage());
		String boostrapContext = String.format("%s-spring-bootstrap.xml", configuration.getWebappContext());
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(JavaCodeGen.class.getResourceAsStream("AppBootstrap.java.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("BOOTSTRAP_PACKAGE", bootstrappackage)
						.addMapEntry("DOMAIN_PACKAGE", configuration.getDomainPackageName())
						.addMapEntry("BOOTSTRAP_CLASSNAME", configuration.getAppBootstrapClassName())
						.addMapEntry("BOOTSTRAP_CONTEXT", boostrapContext)
						.buildMap());
		
		System.out.println(generated);

	}

	public static void writeSpringTemplate(CodeGenConfig config, String templateName) throws IOException {

		String text = IOUtils.toString(SourceCodeUtil.class.getResourceAsStream(templateName));
		Map<String, String> params = new HashMap<String, String>();
		params.put("MAPPER_PACKAGE_PATH", config.getMapperPackageName().replaceAll("\\.", "/"));
		params.put("DOMAIN_PACKAGE", config.getDomainPackageName());
		params.put("MAPPER_PACKAGE", config.getMapperPackageName());
		params.put("REST_CONTROLLER_PACKAGE", config.getRestPackageName());
		String sqlMap = SearchReplaceUtils.searchAndReplace(text, params);
		String fileName = templateName.substring(0, templateName.length() - 5);
		writeCode(config, new File(config.getMyBatisXmlBaseDir()), fileName, sqlMap);

	}
	
	
	public static void  main(String[] args) throws Exception
	{
		CodeGenConfig config = new CodeGenConfig();
		config.setMapperPackageName("com.test.mappers");
		config.setDomainPackageName("com.test.domain");
		config.setJavaBaseDir("target/src/main/java");
		config.setMyBatisXmlBaseDir("target/src/main/java");
		config.setSpringXmlBaseDir("target/src/main/java");
		config.setRestPackageName("com.test.rest");
		config.setAppBootstrapClassName("WLMAppBootstrap");
		config.setAppBootstrapPackage("com.asksunny.wlm");
		config.setWebappContext("wm");
		writeSpringBootstrap(config);
		
	}
	
	

	public static void writeCode(CodeGenConfig config, File dir, String fileName, String code) throws IOException {
		File fj = new File(dir, fileName);
		if (config.isSuffixSequenceIfExists() == false && fj.exists()) {
			throw new IOException("File exists:" + fj.toString());
		} else if (fj.exists()) {
			for (int i = 1; i < Integer.MAX_VALUE; i++) {
				fj = new File(dir, String.format("%s.%03d", fileName, i));
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
}
