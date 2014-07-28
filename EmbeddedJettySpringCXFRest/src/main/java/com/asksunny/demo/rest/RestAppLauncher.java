package com.asksunny.demo.rest;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.asksunny.demo.rest.springcfg.SpringAppConfig;
import com.asksunny.ssl.SecureSocketKeyStore;

public class RestAppLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Server server = new Server();

		//Enable SSL here;
		
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme("https");
		http_config.setSecurePort(8443);
		http_config.setOutputBufferSize(32768);
		http_config.setRequestHeaderSize(8192);
		http_config.setResponseHeaderSize(8192);
		http_config.setSendServerVersion(true);
		http_config.setSendDateHeader(false);
		
		SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStore(SecureSocketKeyStore.getKeyStore());
        sslContextFactory.setKeyStorePassword(SecureSocketKeyStore.getKeyStorePasswordString());
        sslContextFactory.setKeyManagerPassword(SecureSocketKeyStore.getCertificatePasswordString());

        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());        
        ServerConnector sslConnector = new ServerConnector(server,
            new SslConnectionFactory(sslContextFactory,"http/1.1"),
            new HttpConnectionFactory(https_config));
        sslConnector.setPort(8443);
        server.addConnector(sslConnector);
                
		// Register and map the dispatcher servlet
		final ServletHolder servletHolder = new ServletHolder(new CXFServlet());
		final ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
		context.addServlet(servletHolder, "/rest/*");
		context.addEventListener(new ContextLoaderListener());
		
		context.setInitParameter("contextClass",
				AnnotationConfigWebApplicationContext.class.getName());
		context.setInitParameter("contextConfigLocation",
				SpringAppConfig.class.getName());

		server.setHandler(context);
		server.start();
		server.join();

	}

}
