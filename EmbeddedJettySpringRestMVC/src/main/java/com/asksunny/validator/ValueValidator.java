package com.asksunny.validator;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.asksunny.validator.annotation.FieldValidate;

public abstract class ValueValidator {

	private Class<?> fieldType;
	private FieldValidate fieldValidateAnnotation;
	private String fieldName;
	private SimpleDateFormat dateFormat;
	private boolean negate;

	public ValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv) {
		this(fieldType, fieldName, fv, false);		
	}

	public ValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv, boolean neg) {
		this.fieldType = fieldType;
		this.fieldValidateAnnotation = fv;
		this.fieldName = fieldName;
		this.dateFormat = new SimpleDateFormat(fv.format());
		this.negate = neg;
	}

	public abstract ValidationResult validate(Object value);

	public Class<?> getFieldType() {
		return fieldType;
	}

	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}

	public FieldValidate getFieldValidateAnnotation() {
		return fieldValidateAnnotation;
	}

	public void setFieldValidateAnnotation(FieldValidate fieldValidateAnnotation) {
		this.fieldValidateAnnotation = fieldValidateAnnotation;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	protected int valueCompare(String val1, Object val2) {
		int cmpResult = 0;
		if (int.class.isAssignableFrom(getFieldType()) || Integer.class.isAssignableFrom(getFieldType())) {
			cmpResult = Integer.valueOf(val1).compareTo((Integer) val2);
		} else if (long.class.isAssignableFrom(getFieldType()) || Long.class.isAssignableFrom(getFieldType())) {
			cmpResult = Long.valueOf(val1).compareTo((Long) val2);
		} else if (short.class.isAssignableFrom(getFieldType()) || Short.class.isAssignableFrom(getFieldType())) {
			cmpResult = Short.valueOf(val1).compareTo((Short) val2);
		} else if (double.class.isAssignableFrom(getFieldType()) || Double.class.isAssignableFrom(getFieldType())) {
			cmpResult = Double.valueOf(val1).compareTo((Double) val2);
		} else if (float.class.isAssignableFrom(getFieldType()) || Float.class.isAssignableFrom(getFieldType())) {
			cmpResult = Float.valueOf(val1).compareTo((Float) val2);
		} else if (BigInteger.class.isAssignableFrom(getFieldType())) {
			cmpResult = new BigInteger(val1).compareTo((BigInteger) val2);
		} else if (BigDecimal.class.isAssignableFrom(getFieldType())) {
			cmpResult = new BigDecimal(val1).compareTo((BigDecimal) val2);
		} else if (Date.class.isAssignableFrom(getFieldType())) {
			try {
				cmpResult = getDateFormat().parse(val1).compareTo((Date) val2);
			} catch (ParseException e) {
				throw new InvalidDateFormatException(getFieldValidateAnnotation().format(), val1);
			}
		} else if (Calendar.class.isAssignableFrom(getFieldType())) {
			try {
				cmpResult = getDateFormat().parse(val1).compareTo(((Calendar) val2).getTime());
			} catch (ParseException e) {
				throw new InvalidDateFormatException(getFieldValidateAnnotation().format(), val1);
			}
		} else {
			cmpResult = val1.compareTo(val2.toString());
		}
		return cmpResult;
	}
	
	
	@SuppressWarnings("rawtypes")
	protected int sizeCompare(int val1, Object val2)
	{
		if (Map.class.isAssignableFrom(getFieldType())) {
			return val1 - ((Map)val2).size();
		} else if (Collection.class.isAssignableFrom(getFieldType())) {
			return val1-((Collection)val2).size();
		}else if (CharSequence.class.isAssignableFrom(getFieldType())) {
			return val1-((CharSequence)val2).length();
		}else if (getFieldType().isArray()) {
			return val1-Array.getLength(val2);
		}else{
			throw new InvalidObjectTypeException("java.util.Map, java.util.Collection, java.lang.CharSequence, java.util.Array");
		}
	}
	

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public boolean isNegate() {
		return negate;
	}

	public void setNegate(boolean negate) {
		this.negate = negate;
	}

}
