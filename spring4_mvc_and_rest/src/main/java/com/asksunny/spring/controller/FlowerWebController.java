package com.asksunny.spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.asksunny.spring.domain.Flower;

@Controller
@RequestMapping(value = "/html/flower")
public class FlowerWebController {

	private static final Logger logger = LoggerFactory
			.getLogger(FlowerWebController.class);

	@RequestMapping(value = "/{name}", method = RequestMethod.GET)
	public String findFlower(@PathVariable("name") String name, Model model) {
		Flower f = new Flower();
		f.setName(name);
		model.addAttribute("flower", f);
		return "flowerDetail";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String listFlowers(Model model) {
		List<Flower> flowers = new ArrayList<Flower>();
		model.addAttribute("flowers", flowers);
		return "flowerList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String createFlower(Flower flower, BindingResult bndResult,
			Model model) {
		model.addAttribute("flower", flower);
		return "flowerDetail";
	}

}
