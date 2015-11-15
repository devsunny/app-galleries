package com.asksunny.validator;

import com.asksunny.validator.annotation.FieldValidate;

public class WinthinValuesValidator extends ValueValidator {

	private String[] values = null;

	public WinthinValuesValidator(Class<?> type, String fieldName, FieldValidate fv) {
		this(type, fieldName, fv, false);
	}

	public WinthinValuesValidator(Class<?> type, String fieldName, FieldValidate fv, boolean neg) {
		super(type, fieldName, fv, neg);
		this.values = fv.value();
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	@Override
	public ValidationResult validate(Object val) {
		if (getValues() == null || val == null) {
			return new ValidationResult(false, getFieldType(), getFieldName(),
					getFieldValidateAnnotation().failedMessage());
		}
		boolean valid = false;
		for (String cmpVal : getValues()) {
			valid = valueCompare(cmpVal, val) == 0;
			if (valid) {
				break;
			}
		}
		valid = isNegate() ? !valid : valid;
		return new ValidationResult(getClass().getName(), valid, getFieldType(), getFieldName(), val,
				valid ? getFieldValidateAnnotation().successMessage() : getFieldValidateAnnotation().failedMessage());

	}

}
