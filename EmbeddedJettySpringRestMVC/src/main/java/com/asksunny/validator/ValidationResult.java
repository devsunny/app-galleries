package com.asksunny.validator;

public class ValidationResult {

	private boolean success = false;
	private Class<?> className = null;
	private String fieldName = null;
	private String validationMessage = null;
	private Object actualValue = null;
	private String validatorName = null;

	public ValidationResult() {

	}

	public ValidationResult(boolean success, Class<?> className, String fieldName, String validationMessage) {
		super();
		this.success = success;
		this.className = className;
		this.fieldName = fieldName;
		this.validationMessage = validationMessage;
	}

	public ValidationResult(String validatorName, boolean success, Class<?> className, String fieldName,
			Object actualValue, String validationMessage) {
		super();
		this.validatorName = validatorName;
		this.success = success;
		this.className = className;
		this.fieldName = fieldName;
		this.actualValue = actualValue;
		this.validationMessage = validationMessage;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Class<?> getClassName() {
		return className;
	}

	public void setClassName(Class<?> className) {
		this.className = className;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public void setValidationMessage(String validationMessage) {
		this.validationMessage = validationMessage;
	}

	public Object getActualValue() {
		return actualValue;
	}

	public void setActualValue(Object actualValue) {
		this.actualValue = actualValue;
	}

	public String getValidatorName() {
		return validatorName;
	}

	public void setValidatorName(String validatorName) {
		this.validatorName = validatorName;
	}

	@Override
	public String toString() {
		return "ValidationResult [validatorName=" + validatorName + ", success=" + success + ", className=" + className
				+ ", fieldName=" + fieldName + ", actualValue=" + actualValue + ", validationMessage="
				+ validationMessage + "]";
	}

}
