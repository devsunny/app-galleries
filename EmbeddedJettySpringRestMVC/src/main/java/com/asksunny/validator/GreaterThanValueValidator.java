package com.asksunny.validator;

import java.util.Collection;
import java.util.Map;

import com.asksunny.validator.annotation.FieldValidate;

public class GreaterThanValueValidator extends ValueValidator {

	private boolean includeEquals = false;
	private String minValue = null;

	public GreaterThanValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv) {
		this(fieldType, fieldName, fv, false);
	}

	public GreaterThanValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv, boolean neg) {
		super(fieldType, fieldName, fv, false);
		this.includeEquals = neg;

		if (fv.minValue() != null && fv.minValue().trim().length() > 0) {
			minValue = fv.minValue().trim();
		} else if (fv.value().length > 0 && fv.value()[0].trim().length() > 0) {
			minValue = fv.value()[0].trim();
		}
	}

	@Override
	public ValidationResult validate(Object value) {		
		if (getMinValue() == null) {
			return new ValidationResult(Boolean.TRUE, getFieldType(), getFieldName(),
					getFieldValidateAnnotation().successMessage());
		}
		int cmpResult = 0;
		Class<?> clz = value.getClass();
		if (clz.isArray() || CharSequence.class.isAssignableFrom(clz) || Map.class.isAssignableFrom(clz)
				|| Collection.class.isAssignableFrom(clz)) {
			cmpResult = sizeCompare(getFieldValidateAnnotation().minSize(), value);
		} else {
			cmpResult = valueCompare(getMinValue(), value);			
		}
		boolean valid = isIncludeEquals() ? (cmpResult <= 0) : (cmpResult < 0);
		return new ValidationResult(getClass().getName(), valid, getFieldType(), getFieldName(), value,
				valid ? getFieldValidateAnnotation().successMessage() : getFieldValidateAnnotation().failedMessage());
	}

	public boolean isIncludeEquals() {
		return includeEquals;
	}

	public void setIncludeEquals(boolean includeEquals) {
		this.includeEquals = includeEquals;
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}
	
	

}
