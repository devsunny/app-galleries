package com.asksunny.app;

import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppBooststrap {

	public AppBooststrap() {
	}

	public static void main(String[] args) {

		/**
		 * Doing try catch here is very important, especially spring framwork is
		 * used to launch service service We would like to catch all startup
		 * exception and stop the service if bean exception is every happened;
		 * this way it would prevent unexpected behavior during the running; it
		 * also force developer to handle exception nicely.
		 */
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
