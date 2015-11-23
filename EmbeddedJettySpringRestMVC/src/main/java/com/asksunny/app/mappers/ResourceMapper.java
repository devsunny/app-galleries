package com.asksunny.app.mappers;

import java.util.List;

import com.asksunny.app.domain.Resource;

public interface ResourceMapper {

	
	Resource getResourceByName(String name);
	
	
	List<Resource> getResources();

	void insertResource(Resource resrc);

	void updateResource(Resource resrc);

}
