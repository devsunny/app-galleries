package com.asksunny.app.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.asksunny.app.domain.AllocatedResourcePool;
import com.asksunny.app.domain.PriorityGroupPolicy;
import com.asksunny.app.domain.PriorityGroupPolicyComparator;
import com.asksunny.app.domain.Resource;
import com.asksunny.app.domain.ResourceReservaion;

public class ResourceGovernor {

	/**
	 * key - policy Name key - resource name
	 * 
	 */
	private final Map<String, Map<String, AllocatedResourcePool>> resourcePools;

	@Autowired
	private ResourceManagementService resourceManagementService;

	public ResourceGovernor() {
		resourcePools = new ConcurrentHashMap<String, Map<String, AllocatedResourcePool>>(256);
	}

	public void refresh(List<PriorityGroupPolicy> policies) {
		Collections.sort(policies, new PriorityGroupPolicyComparator());
		for (PriorityGroupPolicy policy : policies) {
			List<ResourceReservaion> reservations = policy.getResourceReservations();
			Map<String, AllocatedResourcePool> allocResourcePools = resourcePools
					.get(policy.getPriorityGroupName().toUpperCase());
			if (allocResourcePools == null) {
				allocResourcePools = new ConcurrentHashMap<String, AllocatedResourcePool>(128);
				resourcePools.put(policy.getPriorityGroupName().toUpperCase(), allocResourcePools);
			}
			for (ResourceReservaion reservation : reservations) {
				String name = reservation.getResourceName().toUpperCase();
				AllocatedResourcePool allocResrcPool = allocResourcePools.get(name);
				if (allocResrcPool == null) {
					allocResrcPool = new AllocatedResourcePool();
					allocResrcPool.setResourceName(reservation.getResourceName());
					allocResrcPool.setPolicy(policy);
					allocResourcePools.put(name, allocResrcPool);
				}
				double re = reservation.getReservedCapacity();
				Resource resource = resourceManagementService.findResources(name);
				allocResrcPool.setMaxCapacity(resource.getMaxCapacity());

				double rc = resource.getMaxCapacity() * re;
				double al = resource.getMaxCapacity() - (resource.getReservedCapacity() + rc);
				if (al > 0) {
					resource.setReservedCapacity(resource.getReservedCapacity() + rc);
				} else {
					rc = resource.getMaxCapacity() - resource.getReservedCapacity();
					resource.setReservedCapacity(resource.getMaxCapacity());
				}
				allocResrcPool.setAllocatedCapacity(rc);
			}

		}
	}

	public Map<String, Map<String, AllocatedResourcePool>> getResourcePools() {
		return resourcePools;
	}

	public ResourceManagementService getResourceManagementService() {
		return resourceManagementService;
	}

	public void setResourceManagementService(ResourceManagementService resourceManagementService) {
		this.resourceManagementService = resourceManagementService;
	}

}
