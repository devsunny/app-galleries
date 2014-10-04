package com.asksunny.jdbc4;

import java.util.ArrayList;
import java.util.Collection;

public class InternalDataRow extends ArrayList<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InternalDataRow() {		
	}

	public InternalDataRow(int initialCapacity) {
		super(initialCapacity);		
	}

	public InternalDataRow(Collection<? extends Object> c) {
		super(c);		
	}
	
	public InternalDataRow addColumn(Object value){
		this.add(value);
		return this;
	}

}
