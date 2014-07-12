package com.asksunny.galleries.web.mvc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asksunny.galleries.web.mvc.domain.User;
import com.asksunny.galleries.web.mvc.persistence.UserMapper;


@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;
	
		
	public User getUser(String userid)
	{
		return userMapper.getUserByUserid(userid);
	}
	
	
	public List<User> getAllUser()
	{
		return userMapper.getAllUser();
	}
	
	
	public User getUser(String userid, String password)
	{
		User user = new User();
		user.setUserid(userid);
		user.setPassword(password);
		return userMapper.getUserByUseridAndPassword(user);
	}
	
	@Transactional
	public void updateUser(User user)
	{
		userMapper.updateUser(user);
	}
	
	@Transactional
	public void addUser(User user)
	{
		userMapper.insertUser(user);
	}
	
	public UserService() {
		
	}

}
