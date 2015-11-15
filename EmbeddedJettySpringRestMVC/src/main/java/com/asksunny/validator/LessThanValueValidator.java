package com.asksunny.validator;

import java.util.Collection;
import java.util.Map;

import com.asksunny.validator.annotation.FieldValidate;

public class LessThanValueValidator extends ValueValidator {

	private boolean includeEquals = false;
	private String maxValue = null;

	public LessThanValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv) {
		this(fieldType, fieldName, fv, false);
	}

	public LessThanValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv, boolean neg) {
		super(fieldType, fieldName, fv, false);
		this.includeEquals = neg;

		if (fv.maxValue() != null && fv.maxValue().trim().length() > 0) {
			maxValue = fv.maxValue().trim();
		} else if (fv.value().length > 0 && fv.value()[0].trim().length() > 0) {
			maxValue = fv.value()[0].trim();
		}
	}

	@Override
	public ValidationResult validate(Object value) {
		if (getMaxValue() == null) {
			return new ValidationResult(Boolean.TRUE, getFieldType(), getFieldName(),
					getFieldValidateAnnotation().successMessage());
		}
		int cmpResult = 0;
		Class<?> clz = value.getClass();
		if (clz.isArray() || CharSequence.class.isAssignableFrom(clz) || Map.class.isAssignableFrom(clz)
				|| Collection.class.isAssignableFrom(clz)) {
			cmpResult = sizeCompare(getFieldValidateAnnotation().maxSize(), value);
		} else {
			cmpResult = valueCompare(getMaxValue(), value);
		}
		boolean valid = isIncludeEquals() ? (cmpResult >= 0) : (cmpResult > 0);
		return new ValidationResult(getClass().getName(), valid, getFieldType(), getFieldName(), value,
				valid ? getFieldValidateAnnotation().successMessage() : getFieldValidateAnnotation().failedMessage());
	}

	public boolean isIncludeEquals() {
		return includeEquals;
	}

	public void setIncludeEquals(boolean includeEquals) {
		this.includeEquals = includeEquals;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

}
