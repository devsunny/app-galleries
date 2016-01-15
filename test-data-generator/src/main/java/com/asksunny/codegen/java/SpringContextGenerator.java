package com.asksunny.codegen.java;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.codegen.CodeGenerator;
import com.asksunny.codegen.utils.ParamMapBuilder;
import com.asksunny.codegen.utils.TemplateUtil;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Schema;

public class SpringContextGenerator extends CodeGenerator {

		
	public void doCodeGen() throws IOException {
		StringBuilder mapperBeans = new StringBuilder();
		List<Entity> entities = schema.getAllEntities();
		for (Entity entity : entities) {
			mapperBeans.append(genSpringMyBatisBeanXml(entity)).append("\n");
		}
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream(
						"spring-mybatis-context.xml.tmpl")),
				ParamMapBuilder
						.newBuilder()
						.addMapEntry(
								"MAPPER_PACKAGE_PATH",
								configuration.getMapperPackageName()
										.replaceAll("[\\.]", "/"))
						.addMapEntry("MYBATIS_MAPPERS", mapperBeans.toString())
						.addMapEntry("DOMAIN_PACKAGE_NAME",
								configuration.getDomainPackageName())
						.addMapEntry("REST_PACKAGE_NAME",
								configuration.getRestPackageName()).buildMap());

		String myBatisSpringContext = String.format(
				"%s-spring-mybatis-context.xml",
				configuration.getWebappContext());
		writeCode(new File(configuration.getSpringXmlBaseDir()),
				myBatisSpringContext, generated);

		String uicontext = TemplateUtil.renderTemplate(IOUtils
				.toString(getClass().getResourceAsStream(
						"spring-webui-context.xml.tmpl")), ParamMapBuilder
				.newBuilder().buildMap());
		// System.out.println(uicontext);
		String uiSpringContext = String.format("%s-spring-ui-context.xml",
				configuration.getWebappContext());
		writeCode(new File(configuration.getSpringXmlBaseDir()),
				uiSpringContext, uicontext);

		String bootstrap = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream(
						"spring-jetty-booststrap.xml.tmpl")),
				ParamMapBuilder
						.newBuilder()
						.addMapEntry("WEBAPP_CONTEXT", configuration.getWebappContext())
						.addMapEntry("UI_CONTEXT_XML", uiSpringContext)
						.addMapEntry("MYBATIS_CONTEXT_XML",
								myBatisSpringContext).buildMap());

		String bootstrapSpringContext = String.format(
				"%s-spring-bootstrap-context.xml",
				configuration.getWebappContext());
		writeCode(new File(configuration.getSpringXmlBaseDir()),
				bootstrapSpringContext, bootstrap);

		if (configuration.getAppBootstrapClassName() != null) {
			String bootstrapClass = writeSpringBootstrap();

			File dir = new File(configuration.getJavaBaseDir());
			if (configuration.getAppBootstrapPackage() != null) {
				String fpath = configuration.getAppBootstrapPackage()
						.replaceAll("[\\.]", "/");
				dir = new File(dir, fpath);
			}
			writeCode(
					dir,
					String.format("%s.java",
							configuration.getAppBootstrapClassName()),
					bootstrapClass);
		}

	}

	public String writeSpringBootstrap() throws IOException {

		String bootstrappackage = configuration.getAppBootstrapPackage() == null ? ""
				: String.format("package %s;",
						configuration.getAppBootstrapPackage());
		String boostrapContext = String.format(
				"%s-spring-bootstrap-context.xml",
				configuration.getWebappContext());
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass()
						.getResourceAsStream("AppBootstrap.java.tmpl")),
				ParamMapBuilder
						.newBuilder()
						.addMapEntry("BOOTSTRAP_PACKAGE", bootstrappackage)
						.addMapEntry("DOMAIN_PACKAGE",
								configuration.getDomainPackageName())
						.addMapEntry("BOOTSTRAP_CLASSNAME",
								configuration.getAppBootstrapClassName())
						.addMapEntry("BOOTSTRAP_CONTEXT", boostrapContext)
						.buildMap());

		return generated;

	}

	public String genSpringMyBatisBeanXml(Entity entity) throws IOException {
		String generated = TemplateUtil.renderTemplate(
				IOUtils.toString(getClass().getResourceAsStream(
						"spring-mybatis-mapper.xml.tmpl")),
				ParamMapBuilder
						.newBuilder()
						.addMapEntry("MAPPER_PACKAGE",
								configuration.getMapperPackageName())
						.addMapEntry("ENTITY_VAR_NAME",
								entity.getEntityVarName())
						.addMapEntry("ENTITY_NAME",
								entity.getEntityObjectName()).buildMap());
		return generated;
	}

	public SpringContextGenerator(CodeGenConfig configuration, Schema schema) {
		super(configuration, schema);
	}

}
