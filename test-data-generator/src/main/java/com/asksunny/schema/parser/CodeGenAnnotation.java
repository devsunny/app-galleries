package com.asksunny.schema.parser;

import com.asksunny.codegen.CodeGenType;

public class CodeGenAnnotation {

	private CodeGenType codeGenType = CodeGenType.OTHER;
	
	String format;
	String maxValue;
	String minValue;
	String step;	
	String enumValues;
	String varname;
	String label;
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
	
	

}
