package com.asksunny.rest.converters;

import java.io.IOException;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class PlainTextConverter extends AbstractHttpMessageConverter<Object> {

	public PlainTextConverter() {
		super(MediaType.TEXT_PLAIN);
	}

	public PlainTextConverter(MediaType... supportedMediaTypes) {
		super(supportedMediaTypes);
	}

	public PlainTextConverter(MediaType supportedMediaType) {
		super(supportedMediaType);
	}

	@Override
	protected Object readInternal(Class<? extends Object> arg0, HttpInputMessage arg1)
			throws IOException, HttpMessageNotReadableException {

		return null;
	}

	@Override
	protected boolean supports(Class<?> arg0) {

		return false;
	}

	@Override
	protected void writeInternal(Object arg0, HttpOutputMessage arg1)
			throws IOException, HttpMessageNotWritableException {

	}

}
