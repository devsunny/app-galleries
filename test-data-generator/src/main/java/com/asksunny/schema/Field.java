package com.asksunny.schema;

import java.util.ArrayList;
import java.util.List;

import com.asksunny.codegen.CodeGenType;
import com.asksunny.codegen.java.JavaIdentifierUtil;

public class Field {

	Entity container;
	int jdbcType;
	int scale;
	int precision;
	int displaySize;
	boolean nullable;
	boolean primaryKey;
	String name;
	String varname;
	String label;

	CodeGenType dataType;
	String format;
	String maxValue;
	String minValue;
	String step;
	int fieldIndex;
	String enumValues;

	List<Field> referencedBy = new ArrayList<Field>();
	Field reference;
	boolean unique;

	public Field() {
		super();
	}

	public Field(int jdbcType, int scale, int precision, int displaySize, boolean nullable, String name,
			CodeGenType dataType, String minValue, String maxValue, String format, String step) {
		super();
		this.jdbcType = jdbcType;
		this.scale = scale;
		this.precision = precision;
		this.displaySize = displaySize;
		this.nullable = nullable;
		this.name = name;
		this.dataType = dataType;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.format = format;
		this.step = step;
	}

	public int getFieldIndex() {
		return fieldIndex;
	}

	public void setFieldIndex(int fieldIndex) {
		this.fieldIndex = fieldIndex;
	}

	public static Field newField(int jdbcType, int scale, int precision, int displaySize, boolean nullable, String name,
			CodeGenType dataType, String minValue, String maxValue, String format, String step) {
		return new Field(jdbcType, scale, precision, displaySize, nullable, name, dataType, minValue, maxValue, format,
				step);
	}

	public List<Field> getReferencedBy() {
		return referencedBy;
	}

	public void addReferencedBy(List<Field> referencedBy) {
		this.referencedBy.addAll(referencedBy);
	}

	public void addReferencedBy(Field referencedBy) {
		this.referencedBy.add(referencedBy);
	}

	public Field getReference() {
		return reference;
	}

	public void setReference(Field reference) {
		this.reference = reference;
	}

	public int getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getDisplaySize() {
		return displaySize;
	}

	public void setDisplaySize(int displaySize) {
		this.displaySize = displaySize;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CodeGenType getDataType() {
		return dataType;
	}

	public void setDataType(CodeGenType dataType) {
		this.dataType = dataType;
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

	public boolean isUnqiueEnum() {
		return getDataType() == CodeGenType.ENUM && (isPrimaryKey() || isUnique());
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public Entity getContainer() {
		return container;
	}

	public void setContainer(Entity container) {
		this.container = container;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (this.referencedBy != null) {
			for (Field fd : this.referencedBy) {
				sb.append(String.format("%s.%s", fd.getContainer().getName(), fd.getName())).append(", ");
			}
		}
		if (sb.length() > 2) {
			sb.deleteCharAt(sb.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("]");
		return "Field [container=" + container.getName() + ", name=" + name + ", fieldIndex=" + fieldIndex
				+ ", jdbcType=" + jdbcType + ", precision=" + precision + ", scale=" + scale + ", displaySize="
				+ displaySize + ", nullable=" + nullable + ", dataType=" + dataType + ", format=" + format
				+ ", minValue=" + minValue + ", maxValue=" + maxValue + ", reference=" + reference + ", step=" + step
				+ ", referencedBy=" + sb.toString() + "]\n";
	}

	public String getEnumValues() {
		return enumValues;
	}

	public void setEnumValues(String enumValues) {
		this.enumValues = enumValues;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getVarname() {
		return varname;
	}

	public void setVarname(String varname) {
		if (varname.indexOf("_") != -1) {
			this.varname = JavaIdentifierUtil.toVariableName(varname);
		} else {
			this.varname = varname;
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String uiname) {
		this.label = uiname;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

}
