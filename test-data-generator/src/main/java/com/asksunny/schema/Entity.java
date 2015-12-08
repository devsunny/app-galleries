package com.asksunny.schema;

import java.util.ArrayList;
import java.util.List;

public class Entity {

	private String name;

	private List<Field> fields;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
		for (Field fd : this.fields) {
			fd.setContainer(this);
		}
	}

	public Field findField(String name) {
		if (this.fields != null) {
			for (Field fd : this.fields) {
				if (fd.name != null && fd.name.equalsIgnoreCase(name)) {
					return fd;
				}
			}
		}
		return null;
	}

	public List<Field> getAllReferences() {
		List<Field> refs = new ArrayList<Field>();
		if (this.fields != null) {
			for (Field fd : this.fields) {
				if (fd.reference != null) {
					refs.add(fd);
				}
			}
		}
		return refs;
	}

	public boolean hasReference() {
		if (this.fields != null) {
			for (Field fd : this.fields) {
				if (fd.reference != null) {
					return true;
				}
			}
		}
		return false;

	}

}
