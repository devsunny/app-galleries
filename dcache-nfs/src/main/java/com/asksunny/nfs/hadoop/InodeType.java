package com.asksunny.nfs.hadoop;

public enum InodeType {
	INODE(0), TAG(1), TAGS(2), ID(3), PATHOF(4), PARENT(5), NAMEOF(6), PGET(7), PSET(
			8), CONST(9);

	private final int _id;

	private InodeType(int id) {
		this._id = id;
	}

	public int getType() {
		return this._id;
	}

	public static InodeType valueOf(int id) {
		for (InodeType type : values()) {
			if (type.getType() == id) {
				return type;
			}
		}
		throw new IllegalArgumentException("No such type: " + id);
	}
}
