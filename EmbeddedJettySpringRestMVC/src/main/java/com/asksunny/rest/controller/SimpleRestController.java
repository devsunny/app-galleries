package com.asksunny.rest.controller;

import java.util.Arrays;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.asksunny.app.service.SimpleInMemoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@RestController
public class SimpleRestController {

	
	@Autowired
	private SimpleInMemoryService simpleService;
	
	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	@Qualifier("hazelcast")
	private HazelcastInstance hazelcast;
	
	public SimpleRestController() {		
	}
	
	
	@RequestMapping(value="/greet/{name}", method={RequestMethod.GET})
	@ResponseBody	
	public String greeting(@PathVariable String name)
	{		
		IgniteCache<String, Integer> greetCache = Ignition.ignite().cache("greetCache");			
		Integer t = greetCache.get(name);
		if(t==null){
			t = new Integer(1);
			greetCache.put(name, t);
		}else{
			t = new Integer(t.intValue()+1);
			greetCache.put(name, t);
		}
		return String.format("Hello %s vists %d!", name, t.intValue());
	}
	
	
	@RequestMapping(value="/hazelcast/greet/{name}", method={RequestMethod.GET})
	@ResponseBody	
	public String hazelcastGreet(@PathVariable String name) throws Exception
	{		
		
		IMap<String, Integer> greetCache = hazelcast.getMap( "greetCache" );	
		Integer t = greetCache.get(name);
		if(t==null){
			t = new Integer(1);
			greetCache.put(name, t);
		}else{
			t = new Integer(t.intValue()+1);
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
