package com.asksunny.rest.cxfclient;

import java.util.Arrays;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;

import com.asksunny.ssl.SecureSokcetTrustManagerFactory;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class ProductServiceClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{

				
		WebClient webClient = WebClient
				.create("https://localhost:8443/rest/api/product", Arrays.asList(new Object[]{new JacksonJaxbJsonProvider()}));	
		//-------------------The following should only use in dev---------------------
		HTTPConduit conduit = WebClient.getConfig(webClient).getHttpConduit();
		TLSClientParameters params = conduit.getTlsClientParameters();
		if (params == null) {
			params = new TLSClientParameters();
			conduit.setTlsClientParameters(params);
		}
		params.setTrustManagers(SecureSokcetTrustManagerFactory
				.getTrustManagers());
		params.setDisableCNCheck(true);
		//------------------------------------------------------------------------------
		
		webClient.type(MediaType.APPLICATION_JSON).accept(
				MediaType.APPLICATION_JSON);
		
		//Enable BASIC Authentication;
		// Replace 'user' and 'password' by the actual values
		//String authorizationHeader = "Basic " 
		//    + org.apache.cxf.common.util.Base64Utility.encode("user:password".getBytes());		
		// web clients
		//webClient.header("Authorization", authorizationHeader);		
		
		Response response = webClient.get();		
		Product[] prods = response.readEntity(Product[].class);
		for (int i = 0; i < prods.length; i++) {
			System.out.println(prods[i].getName());
			System.out.println(prods[i].getManfacturedDate());
		}

	}

}
