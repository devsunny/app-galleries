package com.asksunny.app.domain;

public class ResourceReservaion {

	private String resourceName = null;

	private double reservedCapacity = 0;

	public ResourceReservaion() {

	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public double getReservedCapacity() {
		return reservedCapacity;
	}

	public void setReservedCapacity(double reservedCapacity) {
		this.reservedCapacity = reservedCapacity;
	}

	public ResourceReservaion(String resourceName, double reservedCapacity) {
		super();
		this.resourceName = resourceName;
		this.reservedCapacity = reservedCapacity;
	}

}
