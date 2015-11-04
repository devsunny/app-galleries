package com.asksunny.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppBooststrap {

	public AppBooststrap() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		ApplicationContext appContext = new ClassPathXmlApplicationContext("server-app-context.xml");

	}

}
