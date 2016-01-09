package com.asksunny.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {

	private String name;

	private final List<Field> fields = new ArrayList<>();
	private final Map<String, Field> fieldMaps = new HashMap<>();

	public String getName() {
		return name;
	}

	public Entity(String name) {
		super();
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void addField(Field field) {
		field.setContainer(this);
		field.setFieldIndex(this.fields.size());
		this.fields.add(field);
		this.fieldMaps.put(field.getName().toUpperCase(), field);
	}

	public Field getUniqueEnumField() {

		for (Field fd : fields) {
			if (fd.isUnqiueEnum()) {
				return fd;
			}
		}
		return null;
	}

	public void setFields(List<Field> fields) {

		for (Field fd : fields) {
			fd.setContainer(this);
			fd.setFieldIndex(this.fields.size());
			this.fieldMaps.put(fd.getName().toUpperCase(), fd);
		}
		this.fields.addAll(fields);
	}

	public Field findField(String name) {
		if (name != null) {
			return fieldMaps.get(name.trim().toUpperCase());
		}
		return null;
	}

	public List<Field> getAllReferences() {
		List<Field> refs = new ArrayList<Field>();
		if (this.fields != null) {
			for (Field fd : this.fields) {
				if (fd.reference != null) {
					if (!fd.reference.getReferencedBy().contains(fd)) {
						fd.reference.addReferencedBy(fd);
					}
					refs.add(fd.reference);
				}
			}
		}
		return refs;
	}

	public boolean hasReferencedBy() {
		if (this.fields != null) {
			for (Field fd : this.fields) {
				if (fd.getReferencedBy() != null && fd.getReferencedBy().size() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasMultiReferencedBy() {
		if (this.fields != null) {
			for (Field fd : this.fields) {
				if (fd.getReferencedBy() != null && fd.getReferencedBy().size() > 1) {
					return true;
				}
			}
		}
		return false;
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

	@Override
	public String toString() {
		return "Entity [name=" + name + ", fields=\n" + fields + "\n]";
	}

}
