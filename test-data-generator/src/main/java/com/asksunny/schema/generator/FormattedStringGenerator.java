package com.asksunny.schema.generator;

public class FormattedStringGenerator implements Generator<String> {

	
	private String format;
	private boolean nullable;
	
	
	public FormattedStringGenerator(String format, boolean nullable) {
		super();
		this.format = format;
		this.nullable = nullable;
	}

	@Override
	public String nextStringValue() {		
		return nextValue();
	}

	@Override
	public String nextValue() {
		if (nullable && RandomUtil.getInstance().getUnsignedInt(100000) % 13 == 0) {
			return null;
		}
		return RandomUtil.getInstance().getFormattedString(format);
	}

}
