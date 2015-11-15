package com.asksunny.validator;

import com.asksunny.validator.annotation.FieldValidate;

public class BypassValueValidator extends ValueValidator {

	public BypassValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv) {
		super(fieldType, fieldName, fv);
	}

	public BypassValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv, boolean neg) {
		super(fieldType, fieldName, fv, neg);
	}

	@Override
	public ValidationResult validate(Object value) {
		return new ValidationResult(getClass().getName(), !isNegate(), getFieldType(), getFieldName(), value,  (!isNegate())
				? getFieldValidateAnnotation().successMessage() : getFieldValidateAnnotation().failedMessage());
	}

}
