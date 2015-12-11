package com.asksunny.rest.controller;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.asksunny.app.domain.Dashboard;
import com.asksunny.app.domain.Header;
import com.asksunny.app.domain.ResponseMessage;
import com.asksunny.app.domain.Task;

@RestController
public class DashboardController {

	SecureRandom random = new SecureRandom(UUID.randomUUID().toString().getBytes());
	
	@RequestMapping(value = "/s1", method = { RequestMethod.GET })
	@ResponseBody
	public ResponseMessage<Integer> getStep1()
	{
		int ret = Math.abs(random.nextInt(100));
		return new ResponseMessage<Integer>(200, "OK", ret);
	}
	
	
	
	@RequestMapping(value = "/dashboard", method = { RequestMethod.GET })
	@ResponseBody
	public ResponseMessage<Dashboard> getDashboard()
	{
		Header header = new Header();			
		List<Task> waiting = createTasks(Math.abs(random.nextInt(20)), "Waiting");
		List<Task> ready = createTasks(Math.abs(random.nextInt(20)), "Ready");
		List<Task> nofify = createTasks(Math.abs(random.nextInt(20)), "Nofifying");
		List<Task> running = createTasks(Math.abs(random.nextInt(20)), "Running");
		header.setNotfiyingJobs(nofify.size());
		header.setRunningJobs(running.size());
		header.setWatingJobs(waiting.size());
		header.setReadyJobs(ready.size());		
		Dashboard dashboard = new Dashboard(header, waiting, ready, nofify, running);
		return new ResponseMessage<Dashboard>(200, "OK", dashboard);
	}
	
	public List<Task> createTasks(int num, String status)
	{
		List<Task> tasks = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			Task t = new Task();
			t.setName(String.format("%s Task # %d", status, i+1));
			tasks.add(t);
		}		
		return tasks;
	}
	
}
