package com.asksunny.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
@Inherited
public @interface FieldValidate {

	boolean notNull() default false;

	ValidationOperator operator() default ValidationOperator.NONE;

	String[] value() default {};

	String minValue() default "";

	String maxValue() default "";

	int minSize() default 0;

	int maxSize() default 0;

	boolean recursive() default false;

	String failedMessage() default "";

	String successMessage() default "";

	String format() default "yyyy-MM-dd"; // this is only used when type is
											// java.util.Date or
											// java.util.Calendar
}
