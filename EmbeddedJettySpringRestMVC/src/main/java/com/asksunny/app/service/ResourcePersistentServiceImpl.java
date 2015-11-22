package com.asksunny.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asksunny.app.mappers.ResourceMapper;

@Service
public class ResourcePersistentServiceImpl 
{

	@Autowired
	private ResourceMapper resourceMapper;
	
	
	
	
	

	public ResourceMapper getResourceMapper() {
		return resourceMapper;
	}

	public void setResourceMapper(ResourceMapper resourceMapper) {
		this.resourceMapper = resourceMapper;
	}
	
	
	
	
	
}
