package com.asksunny.codegen;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.asksunny.collections.CaselessHashSet;

public class CodeGenConfig {

	public static enum CodeOverwriteStrategy {
		OVERWRITE, IGNORE, SUFFIX_SEQUENCE, RENAME_EXISTING
	};

	String javaBaseDir = "src/main/java";
	String myBatisXmlBaseDir = "src/main/resources";
	String springXmlBaseDir = "src/main/resources";
	String webappContext = "spring";
	String webBaseSrcDir = "src/main/resources/META-INF/app";
	String baseSrcDir = ".";
	String basePackageName = "com.foo";
	
	String domainPackageName;
	String mapperPackageName;
	String restPackageName;
	String schemaFiles = null;
	String angularAppName = "sbAdminApp";
	String appBootstrapPackage;
	String appBootstrapClassName;

	String SSLIssuerDN = "CN=Test Certificate";

	boolean genAngularView = true;
	boolean genAngularRoute = true;
	boolean genAngularController = true;
	boolean genDomainObject = true;
	boolean genMyBatisMapper = true;
	boolean genRestController = true;
	boolean genSpringContext = true;
	boolean genMyBatisSpringBeans = true;
	boolean suffixSequenceIfExists = true;
	boolean genPomXml = true;

	CaselessHashSet includes = new CaselessHashSet();
	CaselessHashSet excludes = new CaselessHashSet();

	CodeOverwriteStrategy overwriteStrategy = CodeOverwriteStrategy.RENAME_EXISTING;

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
		return javaBaseDir == null ? String.format("%s/src/main/java", getBaseSrcDir()) : javaBaseDir;
	}

	public void setJavaBaseDir(String javaBaseDir) {
		this.javaBaseDir = javaBaseDir;
	}

	public String getMyBatisXmlBaseDir() {
		return myBatisXmlBaseDir == null ? String.format("%s/src/main/resources", getBaseSrcDir()) : myBatisXmlBaseDir;
	}

	public void setMyBatisXmlBaseDir(String myBatisXmlBaseDir) {
		this.myBatisXmlBaseDir = myBatisXmlBaseDir;
	}

	public String getDomainPackageName() {
		return domainPackageName == null ? String.format("%s.domain", getBasePackageName()) : domainPackageName;
	}

	public void setDomainPackageName(String domainPackageName) {
		this.domainPackageName = domainPackageName;
	}

	public String getMapperPackageName() {
		return mapperPackageName == null ? String.format("%s.mappers", getBasePackageName()) : mapperPackageName;
	}

	public void setMapperPackageName(String mapperPackageName) {
		this.mapperPackageName = mapperPackageName;
	}

	public String getRestPackageName() {
		return restPackageName == null ? String.format("%s.rest", getBasePackageName()) : restPackageName;
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
		return springXmlBaseDir == null ? String.format("%s/src/main/resources", getBaseSrcDir()) : springXmlBaseDir;
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

	public String getAngularAppName() {
		return angularAppName;
	}

	public void setAngularAppName(String angularAppName) {
		this.angularAppName = angularAppName;
	}

	public String getWebappContext() {
		return webappContext;
	}

	public void setWebappContext(String webappContext) {
		this.webappContext = webappContext;
	}

	public boolean isGenAngularView() {
		return genAngularView;
	}

	public void setGenAngularView(boolean genAngularView) {
		this.genAngularView = genAngularView;
	}

	public boolean isGenAngularRoute() {
		return genAngularRoute;
	}
	
	public boolean isGenAngular()
	{
		return this.isGenAngularController() || this.isGenAngularRoute()||isGenAngularView();
	}

	public void setGenAngularRoute(boolean genAngularRoute) {
		this.genAngularRoute = genAngularRoute;
	}

	public boolean isGenAngularController() {
		return genAngularController;
	}

	public void setGenAngularController(boolean genAngularController) {
		this.genAngularController = genAngularController;
	}

	public String getWebBaseSrcDir() {
		return webBaseSrcDir == null ? String.format("%s/src/main/resources/META-INF/webapp", getBaseSrcDir())
				: webBaseSrcDir;
	}

	public void setWebBaseSrcDir(String webBaseSrcDir) {
		this.webBaseSrcDir = webBaseSrcDir;
	}

	public boolean isGenMyBatisSpringBeans() {
		return genMyBatisSpringBeans;
	}

	public void setGenMyBatisSpringBeans(boolean genMyBatisSpringBeans) {
		this.genMyBatisSpringBeans = genMyBatisSpringBeans;
	}

	public String getAppBootstrapPackage() {
		return appBootstrapPackage == null
				? getBasePackageName() : appBootstrapPackage;
	}

	public void setAppBootstrapPackage(String appBootstrapPackage) {
		this.appBootstrapPackage = appBootstrapPackage;
	}

	public String getAppBootstrapClassName() {
		return appBootstrapClassName;
	}

	public void setAppBootstrapClassName(String appBootstrapClassName) {
		this.appBootstrapClassName = appBootstrapClassName;
	}

	public String getSSLIssuerDN() {
		return SSLIssuerDN;
	}

	public void setSSLIssuerDN(String sSLIssuerDN) {
		SSLIssuerDN = sSLIssuerDN;
	}

	public void setIncludes(CaselessHashSet includes) {
		this.includes = includes;
	}

	public void setExcludes(CaselessHashSet excludes) {
		this.excludes = excludes;
	}

	public String getBaseSrcDir() {
		return baseSrcDir == null ? "generated-src" : baseSrcDir;
	}

	public void setBaseSrcDir(String baseSrcDir) {
		this.baseSrcDir = baseSrcDir;
	}

	public String getBasePackageName() {
		return basePackageName == null ? "com.foo" : basePackageName;
	}

	public void setBasePackageName(String basePackageName) {
		this.basePackageName = basePackageName;
	}

	public boolean isGenPomXml() {
		return genPomXml;
	}

	public void setGenPomXml(boolean genPomXml) {
		this.genPomXml = genPomXml;
	}

}
