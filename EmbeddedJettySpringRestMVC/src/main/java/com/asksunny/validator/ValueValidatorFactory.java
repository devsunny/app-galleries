package com.asksunny.validator;

import java.lang.reflect.Field;

import com.asksunny.validator.annotation.FieldValidate;
import com.asksunny.validator.annotation.ValidationOperator;

public final class ValueValidatorFactory {

	private ValueValidatorFactory() {

	}

	public static ValueValidator createValidator(Field field, FieldValidate fv) {

		if (fv.operator() == ValidationOperator.NONE) {
			return new BypassValueValidator(field.getType(), field.getName(), fv);
		} else if (fv.operator() == ValidationOperator.EQUALS
				|| (fv.operator() == ValidationOperator.WITHIN && fv.value().length == 1)) {
			return new EqualsValueValidator(field.getType(), field.getName(), fv);
		} else if (fv.operator() == ValidationOperator.NOT_EQUALS
				|| (fv.operator() == ValidationOperator.NOT_WITHIN && fv.value().length == 1)) {
			return new WinthinValuesValidator(field.getType(), field.getName(), fv, true);
		} else if (fv.operator() == ValidationOperator.WITHIN || fv.operator() == ValidationOperator.NOT_WITHIN) {
			return new WinthinValuesValidator(field.getType(), field.getName(), fv,
					fv.operator() == ValidationOperator.NOT_WITHIN);
		} else if (fv.operator() == ValidationOperator.BETWEEN || fv.operator() == ValidationOperator.NOT_BETWEEN) {
			return new BetweenValueValidator(field.getType(), field.getName(), fv,
					fv.operator() == ValidationOperator.NOT_BETWEEN);
		} else if (fv.operator() == ValidationOperator.GREATER
				|| fv.operator() == ValidationOperator.GREATER_OR_EQUALS) {
			return new GreaterThanValueValidator(field.getType(), field.getName(), fv,
					fv.operator() == ValidationOperator.GREATER_OR_EQUALS);
		} else if (fv.operator() == ValidationOperator.LESS || fv.operator() == ValidationOperator.LESS_OR_EQUALS) {
			return new LessThanValueValidator(field.getType(), field.getName(), fv,
					fv.operator() == ValidationOperator.LESS_OR_EQUALS);
		} else if (fv.operator() == ValidationOperator.REGEX_MATCH
				|| fv.operator() == ValidationOperator.REGEX_NOT_MATCH) {
			return new RegExMatchValueValidator(field.getType(), field.getName(), fv,
					fv.operator() == ValidationOperator.REGEX_NOT_MATCH);
		} else if (fv.operator() == ValidationOperator.REGEX_CONTAINS
				|| fv.operator() == ValidationOperator.REGEX_NOT_CONTAINS) {
			return new RegExContainsValueValidator(field.getType(), field.getName(), fv,
					fv.operator() == ValidationOperator.REGEX_NOT_CONTAINS);
		} else if (fv.operator() == ValidationOperator.JAVA_BEAN) {
			return new NestedBeanValueValidator(field.getType(), field.getName(), fv);
		} else {			
			return new BypassValueValidator(field.getType(), field.getName(), fv);
		}
	}

}
