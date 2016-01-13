package com.asksunny.schema;

import java.util.Comparator;

public class FieldOrderComparator implements Comparator<Field> {

	public FieldOrderComparator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(Field arg0, Field arg1) {
		return Integer.valueOf(arg0.getOrder()).compareTo(Integer.valueOf(arg1.getOrder()));
	}

}
