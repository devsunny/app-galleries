package com.asksunny.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Schema extends HashMap<String, Entity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Schema(String name) {
		super();
		this.name = name;
	}

	@Override
	public Entity get(Object arg0) {
		return super.get(arg0.toString().toUpperCase());
	}

	@Override
	public Entity put(String arg0, Entity arg1) {
		return super.put(arg0.toUpperCase(), arg1);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Entity> arg0) {
		Set<? extends String> keys = arg0.keySet();
		for (String string : keys) {
			super.put(string.toUpperCase(), arg0.get(string));
		}
	}

	/**
	 * We start with all independent entities that does reference other entity
	 * (foreign key relationship)
	 * 
	 * @return
	 */
	public List<Entity> getIndependentEntities() {
		List<Entity> entities = new ArrayList<Entity>();
		for (Iterator<Entity> iterator = this.values().iterator(); iterator.hasNext();) {
			Entity entity = iterator.next();
			if (!entity.hasReference()) {
				entities.add(entity);
			}
		}
		return entities;
	}

	public void buildRelationship() {
		List<Entity> entities = new ArrayList<Entity>(this.values());
		for (Entity entity : entities) {
			List<Field> refColumn = entity.getAllReferences();
			if (refColumn != null && refColumn.size() > 0) {
				for (Field field : refColumn) {
					Field fd = field.getReference();
					Entity refEntity = this.get(fd.getContainer().getName());
					if (refEntity == null) {
						throw new IllegalArgumentException(
								String.format("Invalid ref at %s:%s", entity.getName(), field.getName()));
					}
					Field refField = refEntity.findField(fd.getName());
					refField.addReferencedBy(field);
				}

			}

		}

	}

}
