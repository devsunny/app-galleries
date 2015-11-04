package com.asksunny.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.asksunny.app.service.SimpleInMemoryService;

@RestController
public class SimpleRestController {

	
	@Autowired
	private SimpleInMemoryService simpleService;
	
	
	public SimpleRestController() {		
	}
	
	
	@RequestMapping(value="/greet/{name}", method={RequestMethod.GET})
	@ResponseBody
	public String greeting(@PathVariable String name)
	{
		return String.format("Hello %s!", name);
	}


	public SimpleInMemoryService getSimpleService() {
		return simpleService;
	}


	public void setSimpleService(SimpleInMemoryService simpleService) {
		this.simpleService = simpleService;
	}

	
	
}
