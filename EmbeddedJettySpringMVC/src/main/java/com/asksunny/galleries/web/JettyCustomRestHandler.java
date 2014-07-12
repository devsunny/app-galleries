package com.asksunny.galleries.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class JettyCustomRestHandler extends AbstractHandler {

	String contextPath;

	public JettyCustomRestHandler() {

	}

	public JettyCustomRestHandler(String contextPath) {
		super();
		this.contextPath = contextPath;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public void handle(String arg0, Request arg1, HttpServletRequest arg2,
			HttpServletResponse arg3) throws IOException, ServletException {
		
		if(arg0.startsWith(getContextPath())){
			arg3.getWriter().write("Invoking Rest Server");			
			arg1.setHandled(true);
		}
		
	}

}
