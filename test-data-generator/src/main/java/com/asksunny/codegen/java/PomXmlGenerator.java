package com.asksunny.codegen.java;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.asksunny.codegen.CodeGenConfig;
import com.asksunny.codegen.CodeGenerator;
import com.asksunny.codegen.utils.ParamMapBuilder;
import com.asksunny.codegen.utils.TemplateUtil;
import com.asksunny.schema.Schema;

public class PomXmlGenerator extends CodeGenerator {

	public static final String GENERATOR_PROJECT_NAME = "data-generator";

	public PomXmlGenerator(CodeGenConfig configuration, Schema schema) {
		super(configuration, schema);
	}

	@Override
	public void doCodeGen() throws IOException {

		File pomFile = new File("pom.xml");
		StringBuilder generator = new StringBuilder();
		if (pomFile.exists() && pomFile.isFile()) {

			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				InputSource in = new InputSource("pom.xml");
				Document doc = factory.newDocumentBuilder().parse(in);
				NodeList dependNodes = doc.getElementsByTagName("dependency");
				int cnt = dependNodes.getLength();
				for (int i = 0; i < cnt; i++) {
					Element el = (Element) dependNodes.item(i);
					if (el.getTextContent().indexOf(GENERATOR_PROJECT_NAME) != -1) {
						generator.append("<dependency>").append(NEW_LINE);
						generator.append("<groupId>")
								.append(el.getElementsByTagName("groupId").item(0).getTextContent())
								.append("</groupId>").append(NEW_LINE);
						generator.append("<artifactId>")
								.append(el.getElementsByTagName("artifactId").item(0).getTextContent())
								.append("</artifactId>").append(NEW_LINE);
						generator.append("<version>")
								.append(el.getElementsByTagName("version").item(0).getTextContent())
								.append("</version>").append(NEW_LINE);
						generator.append("</dependency>").append(NEW_LINE);
					}

				}				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		String pomXml = TemplateUtil.renderTemplate(IOUtils.toString(getClass().getResourceAsStream("pom.xml.tmpl")),
				ParamMapBuilder.newBuilder().addMapEntry("GENERATOR_PROJECT_DEPENDENCY", generator.toString())
						.buildMap());
		writeCode(new File(configuration.getBaseSrcDir()), "pom.xml", pomXml);

	}

}
