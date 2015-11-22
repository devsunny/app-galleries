package com.asksunny.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.asksunny.app.domain.Resource;
import com.asksunny.app.mappers.ResourceMapper;
import com.asksunny.app.service.SimpleInMemoryService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@RestController
public class SimpleRestController {

	@Autowired
	private SimpleInMemoryService simpleService;

	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private ResourceMapper resourceMapper;

	public ResourceMapper getResourceMapper() {
		return resourceMapper;
	}

	public void setResourceMapper(ResourceMapper resourceMapper) {
		this.resourceMapper = resourceMapper;
	}

	@Autowired
	@Qualifier("hazelcast")
	private HazelcastInstance hazelcast;

	public SimpleRestController() {
	}

	@RequestMapping(value = "/registered/resources", method = { RequestMethod.GET })
	@ResponseBody
	public List<Resource> displayAllResources() throws Exception {
		return resourceMapper.getResources();
	}

	@RequestMapping(value = "/hazelcast/greet/{name}", method = { RequestMethod.GET })
	@ResponseBody
	public String hazelcastGreet(@PathVariable String name) throws Exception {

		IMap<String, Integer> greetCache = hazelcast.getMap("greetCache");
		Integer t = greetCache.get(name);
		if (t == null) {
			t = new Integer(1);
			greetCache.put(name, t);
		} else {
			t = new Integer(t.intValue() + 1);
			greetCache.put(name, t);
		}
		return String.format("Hello %s %s vists %d!", name, t.intValue());
	}

	public SimpleInMemoryService getSimpleService() {
		return simpleService;
	}

	public void setSimpleService(SimpleInMemoryService simpleService) {
		this.simpleService = simpleService;
	}

}
