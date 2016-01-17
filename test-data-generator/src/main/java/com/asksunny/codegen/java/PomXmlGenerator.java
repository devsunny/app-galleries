package com.asksunny.codegen.java;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
		String groupId ="com.foo";
		String artifactId = "scaffold-template";
		String version = "0.0.1.SNAPSHOT";
		
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
				groupId = getElementTextValue(doc.getDocumentElement(), "groupId");
				artifactId = getElementTextValue(doc.getDocumentElement(), "artifactId");
				version = getElementTextValue(doc.getDocumentElement(), "version");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		String pomXml = TemplateUtil.renderTemplate(IOUtils.toString(getClass().getResourceAsStream("pom.xml.tmpl")),
				ParamMapBuilder.newBuilder()
				.addMapEntry("MAVEN_GROUP_ID", groupId)
				.addMapEntry("MAVEN_ARTIFACT_ID", artifactId)
				.addMapEntry("MAVEN_VERSION", version)
				.addMapEntry("GENERATOR_PROJECT_DEPENDENCY", generator.toString())
						.buildMap());
		writeCode(new File(configuration.getBaseSrcDir()), "pom.xml", pomXml);

	}
	
	
	protected String getElementTextValue(Element parent, String name)
	{
		NodeList dependNodes = parent.getChildNodes();
		int cnt = dependNodes.getLength();	
		for (int i = 0; i < cnt; i++) {
			Node el =  dependNodes.item(i);
			if(el instanceof Element && ((Element)el).getTagName().equals(name)){
				return el.getTextContent();
			}
		}
		return "";
	}

}
