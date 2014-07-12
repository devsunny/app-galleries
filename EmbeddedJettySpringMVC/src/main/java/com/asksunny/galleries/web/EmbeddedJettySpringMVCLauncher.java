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
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
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
			
			ResourceHandler resource_handler = new ResourceHandler();
			resource_handler.setDirectoriesListed(true);
			resource_handler.setWelcomeFiles(new String[] { "index.html" });
			resource_handler.setResourceBase(".");

			JettySpringHandler springhandler = new JettySpringHandler("/mvc/");			
			JettyCustomRestHandler resthandler = new JettyCustomRestHandler("/rest/");
			
			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { springhandler, resthandler, resource_handler, new DefaultHandler() });
			server.setHandler(handlers);

			server.start();
			server.join();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EmbeddedJettySpringMVCLauncher launcher = new EmbeddedJettySpringMVCLauncher();
		launcher.run();
	}

}
