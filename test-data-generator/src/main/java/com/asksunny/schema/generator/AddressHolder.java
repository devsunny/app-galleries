package com.asksunny.schema.generator;

public class AddressHolder {
	private Address address;
	private int maxUse = 0;
	private int used = 0;

	public Address getAddress() {
		if (used == maxUse || address == null) {
			address = AddressUtils.getInstance().getAddress();
			used = 0;
		}
		used++;
		return address;
	}

	public void registerToUse()
	{
		this.maxUse++;
	}
}
