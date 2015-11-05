package com.asksunny.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.asksunny.app.domain.Resource;

public class ResourceManagementService {

	private Map<String, Resource> resourcesRegistry = null;

	public ResourceManagementService() {
		resourcesRegistry = new ConcurrentHashMap<>(128);
	}

	public List<Resource> register(List<Resource> resrcs) {
		List<Resource> retResrcs = new ArrayList<>();
		if (resrcs != null && resrcs.size() > 0) {
			for (Resource resrc : resrcs) {
				resourcesRegistry.put(resrc.getName().toUpperCase(), resrc);
			}
		}
		retResrcs.addAll(resourcesRegistry.values());
		return retResrcs;
	}

	public List<Resource> update(List<Resource> resrcs) {
		List<Resource> retResrcs = new ArrayList<>();
		if (resrcs != null && resrcs.size() > 0) {
			for (Resource resrc : resrcs) {
				resourcesRegistry.put(resrc.getName().toUpperCase(), resrc);
			}
		}
		retResrcs.addAll(resourcesRegistry.values());
		return retResrcs;
	}

	public List<Resource> viewResources() {
		List<Resource> retResrcs = new ArrayList<>();
		retResrcs.addAll(resourcesRegistry.values());
		return retResrcs;
	}

	public Resource findResources(String name) {
		return resourcesRegistry.get(name.toUpperCase());
	}

}
