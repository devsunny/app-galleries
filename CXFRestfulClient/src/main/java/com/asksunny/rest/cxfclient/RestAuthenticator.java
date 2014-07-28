package com.asksunny.rest.cxfclient;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

public class RestAuthenticator implements ClientRequestFilter {

	String username = null;
	String password = null;
	
	

	public RestAuthenticator(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}



	@Override
	 public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        final String basicAuthentication = getBasicAuthentication();
        headers.add("Authorization", basicAuthentication);

    }

    private String getBasicAuthentication() {
        String authtoken = this.username + ":" + this.password;
        return "BASIC " + org.apache.cxf.common.util.Base64Utility.encode(authtoken.getBytes());
    }

}
