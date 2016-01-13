package com.asksunny.schema;

import java.util.Comparator;

public class FieldDrillDownComparator implements Comparator<Field> {

	public FieldDrillDownComparator() {
	}

	@Override
	public int compare(Field o1, Field o2) {
		return Integer.valueOf(o1.getDrillDown()).compareTo(
				Integer.valueOf(o2.getDrillDown()));
	}

}
