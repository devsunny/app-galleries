package com.asksunny.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.asksunny.app.domain.Resource;
import com.asksunny.app.mappers.ResourceMapper;
import com.asksunny.app.service.ResourceManagementService;

@Controller
@RequestMapping(value = "/web")
public class SimpleWebController {

	@Autowired
	private ResourceManagementService resourceManagementService;
	
	@Autowired
	private ResourceMapper resourceMapper;
	

	public SimpleWebController() {

	}

	@ModelAttribute("technologies")
	public List<String> supportedTechnologies() {
		List<String> list = Arrays
				.asList(new String[] { "Spring Frameworks", "Themeleaf", "Bootstrap", "Angular", "Hazelcast", "Jetty 9.3"});
		return list;
	}

	@ModelAttribute("resources")
	public List<Resource> getResources() {

		return resourceMapper.getResources();
	}

	@ModelAttribute("author")
	public String getAuthor() {

		return "Sunny Liu";
	}

	@RequestMapping({ "/home" })
	public String apphome() {
		try {
			InputStream in = getClass().getResourceAsStream("/META-INF/app/index.html");
			String text = IOUtils.toString(in);
			System.out.println(text);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "apphome";
	}

	public ResourceManagementService getResourceManagementService() {
		return resourceManagementService;
	}

	public void setResourceManagementService(ResourceManagementService resourceManagementService) {
		this.resourceManagementService = resourceManagementService;
	}

	public ResourceMapper getResourceMapper() {
		return resourceMapper;
	}

	public void setResourceMapper(ResourceMapper resourceMapper) {
		this.resourceMapper = resourceMapper;
	}

}
