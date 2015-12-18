package com.asksunny.codegen;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class CodeGenConfig {

	Set<String> ignores = new HashSet<>();
	String javaBaseDir = "src/main/java";
	String myBatisXmlBaseDir = "src/main/resources";
	String domainPackageName;
	String mapperPackageName;
	String restPackageName;
	String schemaFiles = null;

	boolean genDomainObject = true;
	boolean genMyBatisMapper = true;
	boolean genRestController = true;
	boolean genAngularUIElement = false;
	boolean genSpringContext = false;
	boolean suffixSequenceIfExists = true;

	public CodeGenConfig() {

	}

	public Set<String> getIgnores() {
		return ignores;
	}

	public void setIgnores(String ignoresCsv) {
		String[] igs = ignoresCsv.split("\\s*[,;]\\s*");
		for (int i = 0; i < igs.length; i++) {
			ignores.add(igs[i].toUpperCase());
		}
	}

	public boolean shouldIgnore(String tableName) {
		return tableName == null || StringUtils.isBlank(tableName) || this.ignores.contains(tableName.toUpperCase());
	}

	public String getJavaBaseDir() {
		return javaBaseDir;
	}

	public void setJavaBaseDir(String javaBaseDir) {
		this.javaBaseDir = javaBaseDir;
	}

	public String getMyBatisXmlBaseDir() {
		return myBatisXmlBaseDir;
	}

	public void setMyBatisXmlBaseDir(String myBatisXmlBaseDir) {
		this.myBatisXmlBaseDir = myBatisXmlBaseDir;
	}

	public String getDomainPackageName() {
		return domainPackageName;
	}

	public void setDomainPackageName(String domainPackageName) {
		this.domainPackageName = domainPackageName;
	}

	public String getMapperPackageName() {
		return mapperPackageName;
	}

	public void setMapperPackageName(String mapperPackageName) {
		this.mapperPackageName = mapperPackageName;
	}

	public String getRestPackageName() {
		return restPackageName;
	}

	public void setRestPackageName(String restPackageName) {
		this.restPackageName = restPackageName;
	}

	public String getSchemaFiles() {
		return schemaFiles;
	}

	public void setSchemaFiles(String schemaFiles) {
		this.schemaFiles = schemaFiles;
	}

	public boolean isGenDomainObject() {
		return genDomainObject;
	}

	public void setGenDomainObject(boolean genDomainObject) {
		this.genDomainObject = genDomainObject;
	}

	public boolean isGenMyBatisMapper() {
		return genMyBatisMapper;
	}

	public void setGenMyBatisMapper(boolean genMyBatisMapper) {
		this.genMyBatisMapper = genMyBatisMapper;
	}

	public boolean isGenRestController() {
		return genRestController;
	}

	public void setGenRestController(boolean genRestController) {
		this.genRestController = genRestController;
	}

	public boolean isGenAngularUIElement() {
		return genAngularUIElement;
	}

	public void setGenAngularUIElement(boolean genAngularUIElement) {
		this.genAngularUIElement = genAngularUIElement;
	}

	public boolean isGenSpringContext() {
		return genSpringContext;
	}

	public void setGenSpringContext(boolean genSpringContext) {
		this.genSpringContext = genSpringContext;
	}

	public boolean isSuffixSequenceIfExists() {
		return suffixSequenceIfExists;
	}

	public void setSuffixSequenceIfExists(boolean suffixSequenceIfExists) {
		this.suffixSequenceIfExists = suffixSequenceIfExists;
	}

}
