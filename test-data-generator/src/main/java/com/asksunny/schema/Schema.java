package com.asksunny.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Schema extends HashMap<String, Entity> {

	public Schema() {
		super();
	}

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

	public Entity addEntity(Entity arg0) {
		return put(arg0.getName(), arg0);
	}

	public List<Entity> getAllEntities() {
		return new ArrayList<>(this.values());
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
			List<Field> refColumns = entity.getAllReferences();
			if (refColumns != null && refColumns.size() > 0) {
				for (Field refField : refColumns) {
					Entity referenceEntity = null;
					if (refField.getContainer().getFields() == null
							|| refField.getContainer().getFields().size() == 0) {
						referenceEntity = this.get(refField.getContainer().getName());
						refField.setContainer(referenceEntity);
					} else {
						referenceEntity = refField.getContainer();
					}
				}

			}

		}

	}

}
