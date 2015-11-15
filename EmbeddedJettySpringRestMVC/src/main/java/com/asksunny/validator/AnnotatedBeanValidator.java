package com.asksunny.validator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.asksunny.validator.annotation.FieldValidate;
import com.asksunny.validator.annotation.ValidationOperator;

public class AnnotatedBeanValidator {

	private boolean shortCircuit = false;
	private ValidationException error = null;

	public AnnotatedBeanValidator(boolean shortCircuit) {
		this.shortCircuit = shortCircuit;
	}

	public AnnotatedBeanValidator() {
		this(false);
	}

	public boolean validate(Object objInstance)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return checkState(validateValues(objInstance));
	}

	public List<ValidationResult> validateValues(Object objInstance)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (objInstance == null) {
			throw new NullPointerException("Validator cannot validate NULL");
		}
		List<ValidationResult> results = new ArrayList<ValidationResult>();
		Field[] fields = getAllFields(objInstance.getClass(), new ArrayList<Field>(128)).toArray(new Field[0]);

		for (Field field : fields) {
			FieldValidate fv = field.getAnnotation(FieldValidate.class);
			if (fv == null)
				continue;
			Object obj = PropertyUtils.getProperty(objInstance, field.getName());
			if (obj == null && fv.notNull() == false) {
				results.add(new ValidationResult(Boolean.TRUE, objInstance.getClass(), field.getName(),
						fv.failedMessage()));
			}else if (obj == null && fv.notNull() == true) {
				results.add(new ValidationResult(Boolean.FALSE, objInstance.getClass(), field.getName(),
						fv.failedMessage()));
			}else if (obj != null && fv.operator()==ValidationOperator.NONE && fv.notNull() == false){
				results.add(new ValidationResult(Boolean.TRUE, objInstance.getClass(), field.getName(),
						fv.successMessage()));
			}else{			
				ValueValidator validator = ValueValidatorFactory.createValidator(field, fv);
				if (validator != null) {
					results.add(validator.validate(obj));
				} else {

				}
			}
		}
		return results;
	}

	protected boolean checkState(List<ValidationResult> results) {
		for (ValidationResult validationResult : results) {
			if (validationResult.isSuccess() == false) {
				return false;
			}
		}
		return true;
	}

	protected List<Field> getAllFields(Class<?> type, List<Field> fields) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));
		if (type.getSuperclass() != null) {
			fields = getAllFields(type.getSuperclass(), fields);
		}
		return fields;
	}

	public boolean isShortCircuit() {
		return shortCircuit;
	}

	public void setShortCircuit(boolean shortCircuit) {
		this.shortCircuit = shortCircuit;
	}

	public ValidationException getError() {
		return error;
	}

	public void setError(ValidationException error) {
		this.error = error;
	}

}
