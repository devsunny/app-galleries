package com.asksunny.http.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.asksunny.http.rest.SimpleHttpClient.HttpMethod;

public class SimpleRestfulClientBuilder {

	private final SimpleHttpClient httpClient;

	private SimpleRestfulClientBuilder() {
		httpClient = new SimpleHttpClient();
	}

	public static SimpleRestfulClientBuilder newBuilder() {
		return new SimpleRestfulClientBuilder();
	}

	public SimpleRestfulClientBuilder host(String host) {
		this.httpClient.setHost(host);
		return this;
	}

	public SimpleRestfulClientBuilder port(int port) {
		this.httpClient.setPort(port);
		return this;
	}

	public SimpleRestfulClientBuilder uri(String uri) {
		this.httpClient.setUri(uri);
		return this;
	}

	public SimpleRestfulClientBuilder enableSSL(boolean enable) {
		this.httpClient.setEnableSSL(enable);
		return this;
	}

	public SimpleRestfulClientBuilder verifySSLHost(boolean enable) {
		this.httpClient.setVerifySSLHost(enable);
		return this;
	}

	public SimpleRestfulClientBuilder parameter(String name, String value) {
		this.httpClient.setParameter(name, value);
		return this;
	}

	public SimpleRestfulClientBuilder parameters(Map<String, String> params) {
		this.httpClient.setParameters(params);
		return this;
	}

	public SimpleRestfulClientBuilder contentType(String contentType) {
		this.httpClient.setContentType(contentType);
		return this;
	}

	public SimpleRestfulClientBuilder acceptedContentType(String contentType) {
		this.httpClient.setAcceptedContentType(contentType);
		return this;
	}

	public SimpleRestfulClientBuilder requestBody(String requestBody) {
		this.httpClient.setRequestBody(requestBody.getBytes(StandardCharsets.UTF_8));
		return this;
	}

	public SimpleRestfulClientBuilder basicAuthentication(String user, String credential) {
		this.httpClient.setUser(user);
		this.httpClient.setCredential(credential);
		return this;
	}

	public void get(Writer writer) throws HttpException {
		this.httpClient.setHttpMethod(HttpMethod.GET);
		try {
			this.httpClient.httpRequest(writer);
		} catch (UnsupportedEncodingException e) {
			throw new HttpException("UnsupportedEncodingException", e);
		} catch (IOException e) {
			throw new HttpException("Failed to make request", e);
		}
	}

	public String getAsString() throws HttpException {
		this.httpClient.setHttpMethod(HttpMethod.GET);
		try {
			return this.httpClient.httpRequestAsString();
		} catch (UnsupportedEncodingException e) {
			throw new HttpException("UnsupportedEncodingException", e);
		} catch (IOException e) {
			throw new HttpException("Failed to make request", e);
		}
	}

	public void post(Writer writer) throws HttpException {
		this.httpClient.setHttpMethod(HttpMethod.POST);
		try {
			this.httpClient.httpRequest(writer);
		} catch (UnsupportedEncodingException e) {
			throw new HttpException("UnsupportedEncodingException", e);
		} catch (IOException e) {
			throw new HttpException("Failed to make request", e);
		}
	}

	public String postAsString() throws HttpException {
		this.httpClient.setHttpMethod(HttpMethod.POST);
		try {
			return this.httpClient.httpRequestAsString();
		} catch (UnsupportedEncodingException e) {
			throw new HttpException("UnsupportedEncodingException", e);
		} catch (IOException e) {
			throw new HttpException("Failed to make request", e);
		}
	}

	public void put(Writer writer) throws HttpException {
		this.httpClient.setHttpMethod(HttpMethod.PUT);
		try {
			this.httpClient.httpRequest(writer);
		} catch (UnsupportedEncodingException e) {
			throw new HttpException("UnsupportedEncodingException", e);
		} catch (IOException e) {
			throw new HttpException("Failed to make request", e);
		}
	}

	public String putAsString() throws HttpException {
		this.httpClient.setHttpMethod(HttpMethod.PUT);
		try {
			return this.httpClient.httpRequestAsString();
		} catch (UnsupportedEncodingException e) {
			throw new HttpException("UnsupportedEncodingException", e);
		} catch (IOException e) {
			throw new HttpException("Failed to make request", e);
		}
	}

	public void delete(Writer writer) throws HttpException {
		this.httpClient.setHttpMethod(HttpMethod.DELETE);
		try {
			this.httpClient.httpRequest(writer);
		} catch (UnsupportedEncodingException e) {
			throw new HttpException("UnsupportedEncodingException", e);
		} catch (IOException e) {
			throw new HttpException("Failed to make request", e);
		}
	}

	public String deleteAsString() throws HttpException {
		this.httpClient.setHttpMethod(HttpMethod.DELETE);
		try {
			return this.httpClient.httpRequestAsString();
		} catch (UnsupportedEncodingException e) {
			throw new HttpException("UnsupportedEncodingException", e);
		} catch (IOException e) {
			throw new HttpException("Failed to make request", e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		SimpleRestfulClientBuilder.newBuilder().host("www.google.com").getAsString();
		
		
	}
	
	

}
