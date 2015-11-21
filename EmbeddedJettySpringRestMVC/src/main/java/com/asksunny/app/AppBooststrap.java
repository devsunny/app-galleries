package com.asksunny.app;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppBooststrap {

	public AppBooststrap() {
	}

	public static void main(String[] args) {
		// ApplicationContext appContext = new
		// ClassPathXmlApplicationContext(new
		// String[]{"server-app-context.xml"});
		AbstractApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "server-app-context.xml" });
		//appContext.registerShutdownHook();
		//appContext.close();
		//Ignition.start(appContext.getBean(IgniteConfiguration.class));
	}

}
