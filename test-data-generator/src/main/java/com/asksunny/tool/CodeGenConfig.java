public class CodeGenConfig {

	String javaBaseDir;
	String myBatisXMLBaseDir;
	String domainPackageName;
	String mapperPackageName;
	String restPackageName;

	public CodeGenConfig() {
	}

	public String getJavaBaseDir() {
		return javaBaseDir;
	}

	public void setJavaBaseDir(String javaBaseDir) {
		this.javaBaseDir = javaBaseDir;
	}

	public String getMyBatisXMLBaseDir() {
		return myBatisXMLBaseDir;
	}

	public void setMyBatisXMLBaseDir(String myBatisXMLBaseDir) {
		this.myBatisXMLBaseDir = myBatisXMLBaseDir;
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
	
	

}
