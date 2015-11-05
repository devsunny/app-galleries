package com.asksunny.rest.converters;

public @interface TextConvertableField {
	
	int index();
	int lenght() default 0;
	int paddingChar() default ' ';
	String textQuote() default "";
	TextAlignment justify() default TextAlignment.LEFT_JUSTIFY;
	
}
