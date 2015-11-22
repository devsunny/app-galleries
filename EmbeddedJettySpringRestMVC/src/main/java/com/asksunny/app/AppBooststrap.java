package com.asksunny.app;

import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppBooststrap {

	public AppBooststrap() {
	}

	public static void main(String[] args) {
		AbstractApplicationContext appContext = null;
		try {
			appContext = new ClassPathXmlApplicationContext(new String[] { "server-app-context.xml" });
		} catch (BeansException e) {
			if (appContext != null) {
				appContext.close();
			}
			e.printStackTrace();
			System.exit(1);
		}
	}

}
