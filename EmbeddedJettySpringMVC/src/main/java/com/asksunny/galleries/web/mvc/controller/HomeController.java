package com.asksunny.galleries.web.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class HomeController {

	public HomeController() {
		
	}
	
	@RequestMapping(value="/", method={RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String welcome()
	{
		return "Welcome to Embedded Jetty with Spring";
	}

}
