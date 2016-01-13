package com.asksunny.schema;

import java.util.Comparator;

public class FieldGroupLevelComparator implements Comparator<Field> {

	public FieldGroupLevelComparator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(Field arg0, Field arg1) {		
		return Integer.valueOf(arg0.getGroupLevel()).compareTo(Integer.valueOf(arg1.getGroupLevel()));
	}

}
