package com.asksunny.collections;

import java.util.Collection;
import java.util.HashSet;

public class CaselessHashSet extends HashSet<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CaselessHashSet() {
	}

	public CaselessHashSet(Collection<? extends String> arg0) {
		super(arg0);
	}

	public CaselessHashSet(int arg0) {
		super(arg0);
	}

	public CaselessHashSet(int arg0, float arg1) {
		super(arg0, arg1);
	}

	@Override
	public boolean contains(Object o) {
		return super.contains(o.toString().toUpperCase());
	}

	@Override
	public boolean add(String e) {
		return super.add(e.toUpperCase());
	}

}
