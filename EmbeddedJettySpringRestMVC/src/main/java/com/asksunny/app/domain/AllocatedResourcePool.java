package com.asksunny.app.domain;

public class AllocatedResourcePool {

	private PriorityGroupPolicy policy;
	private String resourceName;
	private double maxCapacity;
	private double allocatedCapacity;
	private double usedCapacity;
	
	
	
	public AllocatedResourcePool() {
		
	}



	public PriorityGroupPolicy getPolicy() {
		return policy;
	}



	public void setPolicy(PriorityGroupPolicy policy) {
		this.policy = policy;
	}



	public String getResourceName() {
		return resourceName;
	}



	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
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



	public double getMaxCapacity() {
		return maxCapacity;
	}



	public void setMaxCapacity(double maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

}
