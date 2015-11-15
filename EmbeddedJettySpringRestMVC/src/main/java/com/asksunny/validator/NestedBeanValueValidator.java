package com.asksunny.validator;

import com.asksunny.validator.annotation.FieldValidate;

public class NestedBeanValueValidator extends ValueValidator {

	public NestedBeanValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv) {
		super(fieldType, fieldName, fv);		
	}

	public NestedBeanValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv, boolean neg) {
		super(fieldType, fieldName, fv, neg);		
	}

	@Override
	public ValidationResult validate(Object value) 
	{
		
		return null;
	}

}
