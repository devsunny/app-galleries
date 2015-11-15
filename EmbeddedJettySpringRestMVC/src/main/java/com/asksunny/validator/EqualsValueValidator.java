package com.asksunny.validator;

import com.asksunny.validator.annotation.FieldValidate;

public class EqualsValueValidator extends ValueValidator {

	private String value;

	public EqualsValueValidator(Class<?> type, String fieldName, FieldValidate fv) {
		this(type, fieldName, fv, false);
	}

	public EqualsValueValidator(Class<?> type, String fieldName, FieldValidate fv, boolean neg) {
		super(type, fieldName, fv, neg);
		value = fv.value().length > 0 ? fv.value()[0] : null;
	}

	@Override
	public ValidationResult validate(Object val) {
		if (getValue() == null || val == null) {
			return new ValidationResult(false, getFieldType(), getFieldName(),
					getFieldValidateAnnotation().failedMessage());
		}
		boolean valid = valueCompare(getValue(), val) == 0;
		valid = isNegate() ? !valid : valid;
		return new ValidationResult(getClass().getName(), valid, getFieldType(), getFieldName(), value,
				valid ? getFieldValidateAnnotation().successMessage() : getFieldValidateAnnotation().failedMessage());
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
