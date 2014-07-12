package com.asksunny.galleries.web.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.asksunny.galleries.web.mvc.domain.User;
import com.asksunny.galleries.web.mvc.service.UserService;

@Controller
public class UserServiceController {

	@Autowired
	UserService userService;

	public UserServiceController() {

	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public String getUser(Model model) {		
		model.addAttribute("users", userService.getAllUser());
		return "userlist";
	}

	@RequestMapping(value = "/user/{userid}", method = RequestMethod.GET)
	public String getUser(@PathVariable("userid") String userid, Model model) {
		User user = userService.getUser(userid);
		model.addAttribute("user", user);
		return "userdetail";
	}
	
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public String createOrUpdate(BindingResult bndResult, User user, Model model) {
		User  u = userService.getUser(user.getUserid());
		if(u==null){
			userService.addUser(user);
		}else{
			userService.updateUser(user);
		}	
		model.addAttribute("user", user);
		return "userdetail";
	}
	

}
