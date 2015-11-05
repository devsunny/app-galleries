package com.asksunny.app.domain;

public class ReservedResourcePool {

	private String name;
	private double reservation;
	private double allocatedCapacity;
	private double usedCapacity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getReservation() {
		return reservation;
	}

	public void setReservation(double reservation) {
		this.reservation = reservation;
	}

	public double getAllocatedCapacity() {
		return allocatedCapacity;
	}

	public void setAllocatedCapacity(double allocatedCapacity) {
		this.allocatedCapacity = allocatedCapacity;
	}

	public double getUsedCapacity() {
		return usedCapacity;
	}

	public void setUsedCapacity(double usedCapacity) {
		this.usedCapacity = usedCapacity;
	}

}
