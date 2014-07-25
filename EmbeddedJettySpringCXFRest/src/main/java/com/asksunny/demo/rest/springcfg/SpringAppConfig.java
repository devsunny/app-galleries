package com.asksunny.demo.rest.springcfg;

import java.util.Arrays;

import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.asksunny.demo.rest.EmbeddedJettyCXfSpringRestApp;
import com.asksunny.demo.rest.service.ProductRsService;
import com.asksunny.demo.rest.service.ProductService;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

@Configuration
public class SpringAppConfig {

	@Bean
	@DependsOn("cxf")
	public Server jaxRsServer() {
		JAXRSServerFactoryBean factory = RuntimeDelegate.getInstance()
				.createEndpoint(embeddedJettyCXfSpringRestApp(),
						JAXRSServerFactoryBean.class);
		factory.setServiceBeans(Arrays.<Object> asList(productRsService()));

		// older than CXF 2.7.5
		// factory.setAddress("/" + factory.getAddress());
		// newer CXF 2.7.6 or later
		factory.setAddress(factory.getAddress());
		// otherwise service will not found error occurs.

		factory.setProviders(Arrays.asList(jacksonJsonProvider()));
		return factory.create();
	}

	@Bean(destroyMethod = "shutdown")
	public SpringBus cxf() {
		return new SpringBus();
	}

	@Bean
	public EmbeddedJettyCXfSpringRestApp embeddedJettyCXfSpringRestApp() {
		return new EmbeddedJettyCXfSpringRestApp();
	}

	@Bean
	public ProductService productService() {
		return new ProductService();
	}

	@Bean
	public ProductRsService productRsService() {
		return new ProductRsService();
	}

	@Bean
	public JacksonJaxbJsonProvider jacksonJsonProvider() {
		return new JacksonJaxbJsonProvider();
	}
	
}
