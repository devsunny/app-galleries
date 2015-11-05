package com.asksunny.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.asksunny.app.domain.PriorityGroupPolicy;

public class PolicyManagementService {

	private Map<String, PriorityGroupPolicy> policyRegistry = null;

	public PolicyManagementService() {
		policyRegistry = new ConcurrentHashMap<>(128);
	}

	public List<PriorityGroupPolicy> register(List<PriorityGroupPolicy> policies) {
		List<PriorityGroupPolicy> retResrcs = new ArrayList<>();
		if (policies != null && policies.size() > 0) {
			for (PriorityGroupPolicy resrc : policies) {
				policyRegistry.put(resrc.getPriorityGroupName().toUpperCase(), resrc);
			}
		}
		retResrcs.addAll(policyRegistry.values());
		return retResrcs;
	}

	public List<PriorityGroupPolicy> update(List<PriorityGroupPolicy> policies) {
		List<PriorityGroupPolicy> retResrcs = new ArrayList<>();
		if (policies != null && policies.size() > 0) {
			for (PriorityGroupPolicy resrc : policies) {
				policyRegistry.put(resrc.getPriorityGroupName().toUpperCase(), resrc);
			}
		}
		retResrcs.addAll(policyRegistry.values());
		return retResrcs;
	}

	public List<PriorityGroupPolicy> viewResources() {
		List<PriorityGroupPolicy> retResrcs = new ArrayList<>();
		retResrcs.addAll(policyRegistry.values());
		return retResrcs;
	}

	public PriorityGroupPolicy findPolicy(String name) {
		return policyRegistry.get(name.toUpperCase());
	}

}
