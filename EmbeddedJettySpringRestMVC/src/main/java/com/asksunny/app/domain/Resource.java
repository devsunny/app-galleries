package com.asksunny.app.domain;

import com.asksunny.rest.converters.TextConvertable;
import com.asksunny.rest.converters.TextConvertableField;

@TextConvertable
public class Resource {

	@TextConvertableField(index = 0)
	private String name;

	@TextConvertableField(index = 1)
	private double maxCapacity;
	
	
	@TextConvertableField(index=2)
	private double reservedCapacity = 0D;
	

	public Resource() {

	}

	public Resource(String name, double maxCapacity) {
		super();
		this.name = name;
		this.maxCapacity = maxCapacity;
	}

	public static Resource newResource(String name, double maxCapacity) {
		return new Resource(name, maxCapacity);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(double maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public double getReservedCapacity() {
		return reservedCapacity;
	}

	public void setReservedCapacity(double reservedCapacity) {
		this.reservedCapacity = reservedCapacity;
	}

}
