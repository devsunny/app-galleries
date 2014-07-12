package com.asksunny.galleries.web.mvc.persistence;

import java.util.List;

import com.asksunny.galleries.web.mvc.domain.User;

public interface UserMapper {

	
	List<User> getAllUser();
	
	User getUserByUserid(String userid);

	User getUserByUseridAndPassword(User user);

	void insertUser(User user);

	void updateUser(User user);

}
