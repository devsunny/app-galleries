package com.asksunny.rest.converters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface TextConvertable 
{
	
	TextFormatType format() default TextFormatType.DELIMITED;
	int fieldDelimiter() default ',';
	int recordDelimiter() default '\n';
	String textQuote() default "";
	
}
