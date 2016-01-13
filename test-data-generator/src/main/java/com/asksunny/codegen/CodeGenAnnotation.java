package com.asksunny.codegen;

public class CodeGenAnnotation {

	private CodeGenType codeGenType = CodeGenType.OTHER;
	
	String format;
	String maxValue;
	String minValue;
	String step;	
	String enumValues;
	String varname;
	String label;
	String uitype;
	String ref;
	String itemsPerPage;
	String ignoreView;
	String ignoreRest;
	String groupFunction;
	String groupLevel;
	String groupView;
	String order;
	String orderBy;
	String autogen;
	String drillDown;
	
	
	
	public CodeGenType getCodeGenType() {
		return codeGenType;
	}
	public void setCodeGenType(CodeGenType codeGenType) {
		this.codeGenType = codeGenType;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}
	public String getMinValue() {
		return minValue;
	}
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getEnumValues() {
		return enumValues;
	}
	public void setEnumValues(String enumValues) {
		this.enumValues = enumValues;
	}
	public String getVarname() {
		return varname;
	}
	public void setVarname(String varname) {
		this.varname = varname;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getUitype() {
		return uitype;
	}
	public void setUitype(String uitype) {
		this.uitype = uitype;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getItemsPerPage() {
		return itemsPerPage;
	}
	public void setItemsPerPage(String itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}
	public String getIgnoreView() {
		return ignoreView;
	}
	public void setIgnoreView(String ignoreView) {
		this.ignoreView = ignoreView;
	}
	public String getIgnoreRest() {
		return ignoreRest;
	}
	public void setIgnoreRest(String ignoreRest) {
		this.ignoreRest = ignoreRest;
	}
	public String getGroupFunction() {
		return groupFunction;
	}
	public void setGroupFunction(String groupFunction) {
		this.groupFunction = groupFunction;
	}
	public String getGroupLevel() {
		return groupLevel;
	}
	public void setGroupLevel(String groupLevel) {
		this.groupLevel = groupLevel;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getGroupView() {
		return groupView;
	}
	public void setGroupView(String groupView) {
		this.groupView = groupView;
	}
	public String getAutogen() {
		return autogen;
	}
	public void setAutogen(String autogen) {
		this.autogen = autogen;
	}
	public String getDrillDown() {
		return drillDown;
	}
	public void setDrillDown(String drillDown) {
		this.drillDown = drillDown;
	}
	
	

}
