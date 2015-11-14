package com.asksunny.app;

import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppBooststrap {

	public AppBooststrap() {		
	}

	public static void main(String[] args) {		
		//ApplicationContext appContext = new ClassPathXmlApplicationContext(new String[]{"server-app-context.xml"});
		ApplicationContext appContext = new ClassPathXmlApplicationContext(new String[]{"server-app-context.xml", "ignite-context.xml"});		
		Ignition.start(appContext.getBean(IgniteConfiguration.class));	
	}

}
