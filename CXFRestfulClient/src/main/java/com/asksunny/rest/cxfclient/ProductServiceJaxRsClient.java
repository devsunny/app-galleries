package com.asksunny.rest.cxfclient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.asksunny.ssl.SecureSokcetTrustManagerFactory;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class ProductServiceJaxRsClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{

		
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, SecureSokcetTrustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};	
		Client client = ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier(allHostsValid).build();		
		//client.register(new RestAuthenticator("user", "password"));
		
		client.register(JacksonJaxbJsonProvider.class);
		WebTarget target = client.target("https://localhost:8443/rest/api");		
		target = target.path("product");			
		Invocation.Builder builder = target.request();		
		
		
		Response response = builder.get();		
		Product[] prods = response.readEntity(Product[].class);
		for (int i = 0; i < prods.length; i++) {
			System.out.println(prods[i].getName());
			System.out.println(prods[i].getManfacturedDate());
		}

	}

}
