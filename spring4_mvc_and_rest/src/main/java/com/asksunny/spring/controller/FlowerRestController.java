package com.asksunny.spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asksunny.spring.domain.Flower;


@Controller
@RequestMapping(value="/rest/flower")
public class FlowerRestController {

	private static final Logger logger = LoggerFactory.getLogger(FlowerRestController.class);
	
	@RequestMapping(value = "/rest/flower/{name}", method = RequestMethod.GET)
	public @ResponseBody Flower findFlower(@PathVariable("name") String name) {		
		Flower emp = new Flower();		
		return emp;
	}
	
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<Flower> listFlowers() {		
		List<Flower> flowers =  new ArrayList<Flower>();		
		return flowers;
	}
	
	
	
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Flower createFlower(@RequestBody Flower flower) {	
		
		return flower;
	}

}
