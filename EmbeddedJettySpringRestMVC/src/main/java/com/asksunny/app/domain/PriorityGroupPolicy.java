package com.asksunny.app.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PriorityGroupPolicy {

	private String priorityGroupName;
	private int priority;
	private List<ResourceReservaion> resourceReservations;

	public PriorityGroupPolicy() 
	{

	}

	public PriorityGroupPolicy(String priorityGroupName, int priority, List<ResourceReservaion> resourceReservations) {
		super();
		this.priorityGroupName = priorityGroupName;
		this.priority = priority;
		this.resourceReservations = resourceReservations;
	}

	public PriorityGroupPolicy(String priorityGroupName, int priority, ResourceReservaion resourceReservation,
			ResourceReservaion... reservaions) {
		super();
		this.priorityGroupName = priorityGroupName;
		this.priority = priority;
		this.resourceReservations = new ArrayList<>();
		this.resourceReservations.add(resourceReservation);
		if (reservaions != null && reservaions.length > 0) {
			this.resourceReservations.addAll(Arrays.asList(reservaions));
		}
	}

	public String getPriorityGroupName() {
		return priorityGroupName;
	}

	public void setPriorityGroupName(String priorityGroupName) {
		this.priorityGroupName = priorityGroupName;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public List<ResourceReservaion> getResourceReservations() {
		return resourceReservations;
	}

	public void setResourceReservations(List<ResourceReservaion> resourceReservations) {
		this.resourceReservations = resourceReservations;
	}

}
