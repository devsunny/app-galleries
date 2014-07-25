package com.asksunny.demo.rest;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.asksunny.demo.rest.springcfg.SpringAppConfig;

public class RestAppLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		Server server = new Server( 8080 );

        // Register and map the dispatcher servlet
        final ServletHolder servletHolder = new ServletHolder( new CXFServlet() );
        final ServletContextHandler context = new ServletContextHandler();   
        context.setContextPath( "/" );
        context.addServlet( servletHolder, "/rest/*" );  
        context.addEventListener( new ContextLoaderListener() );

        context.setInitParameter( "contextClass", AnnotationConfigWebApplicationContext.class.getName() );
        context.setInitParameter( "contextConfigLocation", SpringAppConfig.class.getName() );

        server.setHandler( context );
        server.start();
        server.join(); 

	}

}
