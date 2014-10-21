package com.asksunny.ebcdic;

import java.util.ArrayList;
import java.util.List;

public class Entity {

	public static enum Type {RECORD, INTEGER, DECIMAL, STRING, REDEFINES};
	
	private int recordLevel;
	private String name;
	private Type type = Type.RECORD;	
	private Entity redefine;
	private int occurs = 1;
	
	private List<Entity> subentities = new ArrayList<Entity>();
 	
	public Entity() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Entity> getSubentities() {
		return subentities;
	}

	public void setSubentities(List<Entity> subentities) {
		this.subentities = subentities;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getRecordLevel() {
		return recordLevel;
	}

	public void setRecordLevel(int recordLevel) {
		this.recordLevel = recordLevel;
	}

	@Override
	public String toString() {
		return "Entity [recordLevel=" + recordLevel + ", name=" + name
				+ ", type=" + type + "]";
	}

	public Entity getRedefine() {
		return redefine;
	}

	public void setRedefine(Entity redefine) {
		this.redefine = redefine;
	}

	public int getOccurs() {
		return occurs;
	}

	public void setOccurs(int occurs) {
		this.occurs = occurs;
	}
	
	

}
