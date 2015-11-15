package com.asksunny.validator;

import java.util.Collection;
import java.util.Map;

import com.asksunny.validator.annotation.FieldValidate;

public class BetweenValueValidator extends ValueValidator {

	private String maxValue;
	private String minValue;

	public BetweenValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv) {
		this(fieldType, fieldName, fv, false);
	}

	public BetweenValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv, boolean neg) {
		super(fieldType, fieldName, fv, neg);
		this.minValue = fv.minValue();
		this.maxValue = fv.maxValue();
	}

	@Override
	public ValidationResult validate(Object value) {
		boolean valid = false;
		Class<?> clz = value.getClass();
		if (clz.isArray() || CharSequence.class.isAssignableFrom(clz) || Map.class.isAssignableFrom(clz)
				|| Collection.class.isAssignableFrom(clz)) {
			valid = sizeCompare(getFieldValidateAnnotation().minSize(), value) <= 0
					&& sizeCompare(getFieldValidateAnnotation().maxSize(), value) >= 0;
		} else {
			valid = valueCompare(getMinValue(), value) <= 0 && valueCompare(getMaxValue(), value) >= 0;
		}
		valid = isNegate() ? !valid : valid;
		return new ValidationResult(getClass().getName(), valid, getFieldType(), getFieldName(), value,
				valid ? getFieldValidateAnnotation().successMessage() : getFieldValidateAnnotation().failedMessage());
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

}
