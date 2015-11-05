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

import com.asksunny.app.domain.PriorityGroupPolicy;
import com.asksunny.app.domain.ResponseMessage;
import com.asksunny.app.service.PolicyManagementService;
import com.asksunny.app.service.ResourceGovernor;

@RestController
public class PolicyRestController {

	@Autowired
	private PolicyManagementService policyManagementService;

	@Autowired
	private ResourceGovernor resourceGovernor;

	public PolicyRestController() {
	}

	@RequestMapping(value = "/policies", method = { RequestMethod.PUT })
	@ResponseBody
	public ResponseMessage<List<PriorityGroupPolicy>> registerPolicies(
			@RequestBody List<PriorityGroupPolicy> newPolicies) {
		ResponseMessage<List<PriorityGroupPolicy>> response = null;
		for (PriorityGroupPolicy resrc : newPolicies) {
			if (resrc.getPriorityGroupName() == null || resrc.getPriorityGroupName().trim().length() == 0) {
				response = new ResponseMessage<List<PriorityGroupPolicy>>(501, "priorityGroupName cannt be empty",
						policyManagementService.viewResources());
				break;
			} else if (policyManagementService.findPolicy(resrc.getPriorityGroupName()) != null) {
				response = new ResponseMessage<List<PriorityGroupPolicy>>(502,
						String.format("PriorityGroupPolicy [%s] is already exists", resrc.getPriorityGroupName()),
						policyManagementService.viewResources());
				break;
			}
		}

		if (response == null) {
			response = new ResponseMessage<List<PriorityGroupPolicy>>(ResponseMessage.STATUS_OK,
					ResponseMessage.REASON_OK, policyManagementService.register(newPolicies));
		}
		return response;
	}

	@RequestMapping(value = "/policy", method = { RequestMethod.PUT })
	@ResponseBody
	public ResponseMessage<List<PriorityGroupPolicy>> registerPolicy(@RequestBody PriorityGroupPolicy newResource) {

		ResponseMessage<List<PriorityGroupPolicy>> response = null;
		response = new ResponseMessage<List<PriorityGroupPolicy>>(200, ResponseMessage.REASON_OK,
				policyManagementService.register(Collections.singletonList(newResource)));

		return response;
	}

	@RequestMapping(value = "/policies", method = { RequestMethod.POST })
	@ResponseBody
	public ResponseMessage<List<PriorityGroupPolicy>> updatePolicies(
			@RequestBody List<PriorityGroupPolicy> newResources) {
		ResponseMessage<List<PriorityGroupPolicy>> response = new ResponseMessage<List<PriorityGroupPolicy>>(
				ResponseMessage.STATUS_OK, ResponseMessage.REASON_OK, policyManagementService.update(newResources));
		return response;
	}

	@RequestMapping(value = "/policies", method = { RequestMethod.GET })
	@ResponseBody
	public ResponseMessage<List<PriorityGroupPolicy>> list() {
		ResponseMessage<List<PriorityGroupPolicy>> response = new ResponseMessage<List<PriorityGroupPolicy>>(
				ResponseMessage.STATUS_OK, ResponseMessage.REASON_OK, policyManagementService.viewResources());
		return response;
	}

	@RequestMapping(value = "/policy/{policyName}", method = { RequestMethod.GET })
	@ResponseBody
	public ResponseMessage<PriorityGroupPolicy> list(@PathVariable String policyName) {
		PriorityGroupPolicy p = policyManagementService.findPolicy(policyName);
		ResponseMessage<PriorityGroupPolicy> response = null;
		if (p == null) {
			response = new ResponseMessage<PriorityGroupPolicy>(404,
					String.format("PriorityGroupPolicy [%s] could not be found ", policyName), null);
		} else {
			response = new ResponseMessage<PriorityGroupPolicy>(ResponseMessage.STATUS_OK, ResponseMessage.REASON_OK,
					p);
		}
		return response;
	}

	public PolicyManagementService getPolicyManagementService() {
		return policyManagementService;
	}

	public void setPolicyManagementService(PolicyManagementService policyManagementService) {
		this.policyManagementService = policyManagementService;
	}

	public ResourceGovernor getResourceGovernor() {
		return resourceGovernor;
	}

	public void setResourceGovernor(ResourceGovernor resourceGovernor) {
		this.resourceGovernor = resourceGovernor;
	}

}
