package com.asksunny.galleries.web;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.asksunny.ssl.SecureSocketKeyStore;

public class EmbeddedJettySpringMVCLauncher {

	int port;
	boolean enableSsl;

	public EmbeddedJettySpringMVCLauncher() {
		this(9090, false);
	}

	public EmbeddedJettySpringMVCLauncher(boolean enableSsl) {
		this(9090, enableSsl);
	}

	public EmbeddedJettySpringMVCLauncher(int port, boolean enableSsl) {
		this.port = port;
		this.enableSsl = enableSsl;
	}

	public void run() {

		try {
			// Create a basic jetty server object without declaring the port.
			// Since we are configuring connectors
			// directly we'll be setting ports on those connectors.
			Server server = new Server();
			HttpConfiguration http_config = new HttpConfiguration();
			http_config.setSecureScheme("https");
			http_config.setSecurePort(this.port);
			http_config.setOutputBufferSize(32768);

			ServerConnector serverConnector = null;
			if (this.enableSsl) {
				SslContextFactory sslContextFactory = new SslContextFactory();
				sslContextFactory.setKeyStore(SecureSocketKeyStore
						.getKeyStore());
				sslContextFactory.setKeyStorePassword(SecureSocketKeyStore
						.getKeyStorePasswordString());
				sslContextFactory.setKeyManagerPassword(SecureSocketKeyStore
						.getCertificatePasswordString());

				HttpConfiguration https_config = new HttpConfiguration(
						http_config);
				https_config.addCustomizer(new SecureRequestCustomizer());
				serverConnector = new ServerConnector(
						server,
						new SslConnectionFactory(sslContextFactory, "http/1.1"),
						new HttpConnectionFactory(https_config));
				serverConnector.setPort(this.port);
				serverConnector.setIdleTimeout(500000);
			} else {
				serverConnector = new ServerConnector(server,
						new HttpConnectionFactory(http_config));
				serverConnector.setPort(this.port);
				serverConnector.setIdleTimeout(30000);
			}
			server.setConnectors(new Connector[] { serverConnector });
			String applocation = new ClassPathResource("webapp").getURI().toString();
			ResourceHandler resource_handler = createStaticResourceHandler(applocation, true, new String[] { "index.html" });
			WebAppContext springContext = createSpringWebAppContext(applocation, "/mvc", "/*", new ClassPathResource("webapp/WEB-INF/spring/Spring-WebAppContext.xml").getURI().toString(), "dev");
		

			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { resource_handler, springContext,
					 new DefaultHandler() });			
			server.setHandler(handlers);

			server.start();
			server.join();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}

	}
	
	
	protected static WebAppContext createSpringWebAppContext(String webAppLocation, String contextPath,
			String springUrlMapping, String configPackagePath, String profile)
	{
		WebApplicationContext springContext = createSpringContext(configPackagePath, profile);
		WebAppContext jettyWebContext = new WebAppContext();
		jettyWebContext.setErrorHandler(null);
		jettyWebContext.setResourceBase(webAppLocation);
		jettyWebContext.setContextPath(contextPath);
		jettyWebContext.addServlet(new ServletHolder(new DispatcherServlet(springContext)), springUrlMapping);
		jettyWebContext.addEventListener(new ContextLoaderListener(springContext));		
		return jettyWebContext;
	}

	

	protected static ResourceHandler createStaticResourceHandler(String resourceBase,
			boolean allowDirListing, String[] welcomeFiles) {
		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(allowDirListing);
		if(welcomeFiles!=null) resource_handler.setWelcomeFiles(welcomeFiles);
		resource_handler.setResourceBase(resourceBase);		
		return resource_handler;
	}

	protected static WebApplicationContext createSpringContext(
			String configPackagePath, String profile) {		
		XmlWebApplicationContext context = new XmlWebApplicationContext();			
		context.setConfigLocation(configPackagePath);		
		if (profile != null)
			context.getEnvironment().setDefaultProfiles(profile);
		return context;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EmbeddedJettySpringMVCLauncher launcher = new EmbeddedJettySpringMVCLauncher();
		launcher.run();
	}

}
