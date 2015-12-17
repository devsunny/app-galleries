package com.asksunny.schema;

import java.util.ArrayList;
import java.util.List;

public class Field {

	Entity container;
	int jdbcType;
	int scale;
	int precision;
	int displaySize;
	boolean nullable;
	boolean primaryKey;
	String name;
	String objname;

	DataGenType dataType;
	String format;
	String maxValue;
	String minValue;
	String step;	
	int fieldIndex;
	String enumValues;

	List<Field> referencedBy = new ArrayList<Field>();
	Field reference;

	public Field() {
		super();
	}

	public Field(int jdbcType, int scale, int precision, int displaySize, boolean nullable, String name,
			DataGenType dataType, String minValue, String maxValue, String format, String step) {
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
			DataGenType dataType, String minValue, String maxValue, String format, String step) {
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

	public DataGenType getDataType() {
		return dataType;
	}

	public void setDataType(DataGenType dataType) {
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
		return "Field [container=" + container.getName() + ", name=" + name + ", jdbcType=" + jdbcType + ", precision="
				+ precision + ", scale=" + scale + ", displaySize=" + displaySize + ", nullable=" + nullable
				+ ", dataType=" + dataType + ", format=" + format + ", minValue=" + minValue + ", maxValue=" + maxValue
				+ ", reference=" + reference + ", step=" + step +  ", referencedBy="
				+ referencedBy + "]";
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

	public String getObjname() {
		return objname;
	}

	public void setObjname(String objname) {
		this.objname = objname;
	}

}
