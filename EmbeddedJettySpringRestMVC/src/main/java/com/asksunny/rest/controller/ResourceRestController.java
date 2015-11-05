package com.asksunny.rest.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.asksunny.app.domain.Resource;
import com.asksunny.app.domain.ResponseMessage;
import com.asksunny.app.service.ResourceManagementService;

@RestController
public class ResourceRestController {

	@Autowired
	private ResourceManagementService resourceManagementService;

	public ResourceRestController() {
	}

	@RequestMapping(value = "/resources", method = { RequestMethod.PUT })
	@ResponseBody
	public ResponseMessage<List<Resource>> registerResources(@RequestBody List<Resource> newResources) {
		ResponseMessage<List<Resource>> response = null;
		for (Resource resrc : newResources) {
			if (resrc.getName() == null || resrc.getName().trim().length() == 0) {
				response = new ResponseMessage<List<Resource>>(501, "resource name cannt be empty", resourceManagementService.viewResources());
				break;
			} else if (resourceManagementService.findResources(resrc.getName()) != null) {
				response = new ResponseMessage<List<Resource>>(502,
						String.format("%s is already exists", resrc.getName()),
						resourceManagementService.viewResources());
				break;
			}
		}
		if (response == null) {
			response = new ResponseMessage<List<Resource>>(ResponseMessage.STATUS_OK, ResponseMessage.REASON_OK,
					resourceManagementService.register(newResources));
		}
		return response;
	}

	@RequestMapping(value = "/resource", method = { RequestMethod.PUT })
	@ResponseBody
	public ResponseMessage<List<Resource>> registerResource(@RequestBody Resource newResource) {
		ResponseMessage<List<Resource>> response = new ResponseMessage<List<Resource>>(ResponseMessage.STATUS_OK,
				ResponseMessage.REASON_OK, resourceManagementService.register(Collections.singletonList(newResource)));
		return response;
	}

	@RequestMapping(value = "/resources", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseMessage<List<Resource>> updateResources(@RequestBody List<Resource> newResources) {
		ResponseMessage<List<Resource>> response = new ResponseMessage<List<Resource>>(ResponseMessage.STATUS_OK,
				ResponseMessage.REASON_OK, resourceManagementService.update(newResources));
		return response;
	}

	@RequestMapping(value = "/resources", method = { RequestMethod.GET })
	@ResponseBody
	public ResponseMessage<List<Resource>> list() {
		ResponseMessage<List<Resource>> response = new ResponseMessage<List<Resource>>(ResponseMessage.STATUS_OK,
				ResponseMessage.REASON_OK, resourceManagementService.viewResources());
		return response;
	}

	@RequestMapping(value = "/resource/{resourceName}", method = { RequestMethod.GET })
	@ResponseBody
	public ResponseMessage<Resource> list(@PathVariable String resourceName) {
		Resource r = resourceManagementService.findResources(resourceName);
		ResponseMessage<Resource> response = null;
		if (r == null) {
			response = new ResponseMessage<Resource>(404, String.format("Resource [%s] could not be found ", resourceName), null);
		} else {
			response = new ResponseMessage<Resource>(ResponseMessage.STATUS_OK, ResponseMessage.REASON_OK, r);
		}
		return response;
	}

	public ResourceManagementService getResourceManagementService() {
		return resourceManagementService;
	}

	public void setResourceManagementService(ResourceManagementService resourceManagementService) {
		this.resourceManagementService = resourceManagementService;
	}

}
