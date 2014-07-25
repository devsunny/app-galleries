package com.asksunny.demo.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
public class EmbeddedJettyCXfSpringRestApp extends Application {

	// do not overwrite the implementation,spring will take care of it
	// Add rest service class here will cause rest service fault
	// @Override
	// public Set<Class<?>> getClasses() {
	// // TODO Auto-generated method stub
	// return super.getClasses();
	// }

}
