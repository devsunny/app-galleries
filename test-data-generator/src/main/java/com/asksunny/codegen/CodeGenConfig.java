package com.asksunny.codegen;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.asksunny.collections.CaselessHashSet;

public class CodeGenConfig {

	public static enum CodeOverwriteStrategy {
		OVERWRITE, IGNORE, SUFFIX_SEQUENCE
	};

	String javaBaseDir = "src/main/java";
	String myBatisXmlBaseDir = "src/main/resources";
	String springXmlBaseDir = "src/main/resources";
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

	CaselessHashSet includes = new CaselessHashSet();
	CaselessHashSet excludes = new CaselessHashSet();

	CodeOverwriteStrategy overwriteStrategy = CodeOverwriteStrategy.IGNORE;

	public CodeGenConfig() {

	}

	public Set<String> getIgnores() {
		return excludes;
	}

	public void setIgnores(String ignoresCsv) {
		String[] igs = ignoresCsv.split("\\s*[,;]\\s*");
		for (int i = 0; i < igs.length; i++) {
			excludes.add(igs[i]);
		}
	}

	public void setIncludes(String includessCsv) {
		String[] igs = includessCsv.split("\\s*[,;]\\s*");
		for (int i = 0; i < igs.length; i++) {
			includes.add(igs[i]);
		}
	}
	
	

	public boolean shouldIgnore(String tableName) {
		return tableName == null || StringUtils.isBlank(tableName) || this.excludes.contains(tableName);
	}

	public boolean shouldInclude(String tableName) {
		return tableName != null && (!StringUtils.isBlank(tableName)) && this.includes.contains(tableName);
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

	public String getSpringXmlBaseDir() {
		return springXmlBaseDir;
	}

	public void setSpringXmlBaseDir(String springXmlBaseDir) {
		this.springXmlBaseDir = springXmlBaseDir;
	}

	public CodeOverwriteStrategy getOverwriteStrategy() {
		return overwriteStrategy;
	}

	public void setOverwriteStrategy(CodeOverwriteStrategy overwriteStrategy) {
		this.overwriteStrategy = overwriteStrategy;
	}

	public CaselessHashSet getIncludes() {
		return includes;
	}

	public CaselessHashSet getExcludes() {
		return excludes;
	}

}
