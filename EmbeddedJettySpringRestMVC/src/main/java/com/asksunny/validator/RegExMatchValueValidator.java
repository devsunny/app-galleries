package com.asksunny.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.asksunny.validator.annotation.FieldValidate;

public class RegExMatchValueValidator extends ValueValidator {

	private Pattern[] paterns = null;

	public RegExMatchValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv) {
		this(fieldType, fieldName, fv, false);
	}

	public RegExMatchValueValidator(Class<?> fieldType, String fieldName, FieldValidate fv, boolean neg) {
		super(fieldType, fieldName, fv, neg);
		if (fv.value().length > 0) {
			paterns = new Pattern[fv.value().length];
			for (int i = 0; i < paterns.length; i++) {
				paterns[i] = Pattern.compile(fv.value()[i]);
			}
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ValidationResult validate(Object value) {

		if (paterns == null || paterns.length == 0) {
			return new ValidationResult(getClass().getName(), true, getFieldType(), getFieldName(), value,
					getFieldValidateAnnotation().successMessage());
		}
		boolean valid = false;
		Class<?> clz = value.getClass();
		if (clz.isArray()) {
			Object[] ar = (Object[]) value;
			valid = allMatch(Arrays.asList(ar));
		} else if (List.class.isAssignableFrom(clz)) {
			valid = allMatch((List) value);
		} else if (Collection.class.isAssignableFrom(clz)) {
			valid = allMatch(new ArrayList((Collection) value));
		} else {
			for (int i = 0; i < paterns.length; i++) {
				valid = paterns[i].matcher(value.toString()).matches();
				if (valid) {
					break;
				}
			}
		}

		valid = isNegate() ? !valid : valid;
		return new ValidationResult(getClass().getName(), valid, getFieldType(), getFieldName(), value,
				valid ? getFieldValidateAnnotation().successMessage() : getFieldValidateAnnotation().failedMessage());
	}

	@SuppressWarnings("rawtypes")
	public boolean allMatch(List objs) {
		boolean ma = true;
		for (Object object : objs) {
			String val = object.toString();
			boolean m = false;
			for (int i = 0; i < paterns.length; i++) {
				m = paterns[i].matcher(val).matches();
				if (m) {
					break;
				}
			}
			ma = m;
			if (!ma) {
				break;
			}
		}
		return ma;
	}

}
