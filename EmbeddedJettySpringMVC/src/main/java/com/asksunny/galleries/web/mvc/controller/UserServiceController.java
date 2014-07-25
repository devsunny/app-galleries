package com.asksunny.galleries.web.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.asksunny.galleries.web.mvc.domain.User;
import com.asksunny.galleries.web.mvc.service.UserService;

@Controller
@RequestMapping(value = "/user")
public class UserServiceController {

	@Autowired
	UserService userService;

	public UserServiceController() {

	}

	@RequestMapping(method = RequestMethod.GET)
	public String getUser(Model model) {		
		model.addAttribute("users", userService.getAllUser());
		return "userlist";
	}

	@RequestMapping(value = "/{userid}", method = RequestMethod.GET)
	public String getUser(@PathVariable("userid") String userid, Model model) {
		if(userid.equalsIgnoreCase("new")){
			User user = new User();
			model.addAttribute("user", user);
		}else{
			User user = userService.getUser(userid);
			model.addAttribute("user", user);
		}		
		return "userdetail";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String createOrUpdate(@ModelAttribute User user, BindingResult bndResult,  Model model) {
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
