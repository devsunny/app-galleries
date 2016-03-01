package com.asksunny.http.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SimpleHttpClient {

	public static enum HttpMethod {
		POST, GET, DELETE, PUT
	}

	public static final String CRLF = "\r\n";
	public static final String QUESTION_MARK = "?";
	public static final String AMP = "&";
	public static final String EQUALS = "=";
	public static final String JSON = "application/json";
	private String host;
	private int port = 443;
	private String uri = "/";
	private Map<String, String> parameters;
	private byte[] requestBody;
	private String contentType;
	private String acceptedContentType = "*/*";
	private boolean enableSSL = true;
	private boolean verifySSLHost  = false;
	private String user = null;
	private String credential = "";
	private HttpMethod httpMethod = HttpMethod.GET;
	public static final String SSL_ALGO = "TLS";

	public SimpleHttpClient() {
		parameters = new HashMap<String, String>();
	}

	protected String createHttpRequestUri() throws UnsupportedEncodingException {
		StringWriter sw = new StringWriter();
		sw.write(uri);
		if ((httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE) && parameters != null
				&& parameters.size() > 0) {
			if (!uri.contains(AMP)) {
				if (!uri.contains(QUESTION_MARK)) {
					sw.write(QUESTION_MARK);
				}
			} else if (!uri.endsWith(AMP)) {
				sw.write(AMP);
			}
			for (String key : parameters.keySet()) {
				sw.write(URLEncoder.encode(key, "UTF-8"));
				sw.write(EQUALS);
				sw.write(URLEncoder.encode(parameters.get(key), "UTF-8"));
				sw.write(AMP);
			}

		}
		sw.flush();
		return sw.toString();

	}

	protected String createHttpHeader() throws UnsupportedEncodingException {
		StringWriter sw = new StringWriter();
		sw.write(String.format("%s %s HTTP/1.1", httpMethod.toString(), createHttpRequestUri()));
		sw.write(CRLF);
		sw.write(String.format("Host: %s", this.host));
		sw.write(CRLF);
		if (this.user != null) {
			sw.write(String.format("Authorization: Basic %s",
					Base64Codec.base64Encode(String.format("%s:%s", this.user, this.credential))));
			sw.write(CRLF);
		}
		if (this.acceptedContentType != null) {
			sw.write(String.format("Accept: %s", this.acceptedContentType));
		} else {
			sw.write("Accept:  *.*");
		}
		sw.write(CRLF);
		if (httpMethod == HttpMethod.POST || httpMethod == HttpMethod.PUT) {
			if (requestBody != null && requestBody.length > 0) {
				sw.write(String.format("Content-Type: %s", (this.contentType == null ? JSON : this.contentType)));
				sw.write(CRLF);
				sw.write(String.format("Content-Length: %d", requestBody.length));
				sw.write(CRLF);
			} else if (parameters != null && parameters.size() > 0) {
				StringWriter psw = new StringWriter();
				Set<String> keys = parameters.keySet();
				int size = keys.size();
				for (String key : keys) {
					size--;
					psw.write(URLEncoder.encode(key, "UTF-8"));
					psw.write(EQUALS);
					psw.write(URLEncoder.encode(parameters.get(key), "UTF-8"));
					if (size > 0) {
						psw.write(AMP);
					}
				}
				psw.flush();
				this.requestBody = psw.toString().getBytes(StandardCharsets.UTF_8);
				sw.write("Content-Type: application/x-www-form-urlencoded");
				sw.write(CRLF);
				sw.write(String.format("Content-Length: %d", requestBody.length));
				sw.write(CRLF);
			}
		}
		sw.flush();
		return sw.toString();

	}

	public String httpRequestAsString() throws IOException, UnsupportedEncodingException {
		StringWriter sw = new StringWriter();
		httpRequest(sw);
		return sw.toString();
	}

	public void httpRequest(Writer responseWriter) throws IOException, UnsupportedEncodingException {

		Socket httpSocket = null;
		try {
			TrustManager[] tm = new TrustManager[] { new TrustAllManager(this.host, this.verifySSLHost) };
			SSLContext context = SSLContext.getInstance(SSL_ALGO);
			context.init(new KeyManager[0], tm, new SecureRandom());
			SSLSocketFactory sslSocketFactory = (SSLSocketFactory) context.getSocketFactory();
			httpSocket = sslSocketFactory.createSocket(this.host, this.port);
			OutputStream out = httpSocket.getOutputStream();
			String header = createHttpHeader();
			//System.out.println(header);
			out.write(header.getBytes(StandardCharsets.UTF_8));
			if (this.requestBody != null && this.requestBody.length > 0) {
				out.write(CRLF.getBytes(StandardCharsets.UTF_8));
				out.write(this.requestBody);
			} else {
				out.write(CRLF.getBytes(StandardCharsets.UTF_8));
			}
			out.flush();
			String line = null;
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(httpSocket.getInputStream(), StandardCharsets.UTF_8));
			line = reader.readLine();
			//System.out.println(line);
			HttpStatus status = new HttpStatus(line);
			if (status.getStatusCode() != 200) {
				throw new HttpException(status.getMessage(), status.getStatusCode());
			}
			int length = 0;
			boolean chunked = false;

			while ((line = reader.readLine()) != null) {
				if (line.length() == 0) {
					break;
				} else if (line.equalsIgnoreCase("Transfer-Encoding: chunked")) {
					chunked = true;

				} else if (line.startsWith("Content-Length:")) {
					length = Integer.valueOf(line.substring(15).trim());
				}
			}

			if (chunked) {
				while (true) {
					line = reader.readLine();
					if (line.matches("^[0-9a-fA-F]+$")) {
						if (line.equals("0")) {
							break;
						}
						int clen = Integer.valueOf(line, 16);
						char[] buf = new char[clen];
						int s = 0;
						while (clen > 0) {
							int rlen = reader.read(buf, s, clen);
							clen = clen - rlen;
						}
						responseWriter.write(buf);
						responseWriter.flush();
						reader.readLine();
					}
				}
			} else {
				char[] buf = new char[length];
				int s = 0;
				while (length > 0) {
					int len = reader.read(buf, 0, length);
					length = length - len;
					s = s + len;
				}
				responseWriter.write(buf);
				responseWriter.flush();
			}
			reader.close();
		} catch (KeyManagementException e) {
			throw new HttpException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new HttpException(e);
		} finally {
			if (httpSocket != null) {
				httpSocket.close();
			}
		}
	}

	

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameter(String name, String value) {
		this.parameters.put(name, value);
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters.putAll(parameters);
	}

	public byte[] getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(byte[] requestBody) {
		this.requestBody = requestBody;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getAcceptedContentType() {
		return acceptedContentType;
	}

	public void setAcceptedContentType(String acceptedContentType) {
		this.acceptedContentType = acceptedContentType;
	}

	public boolean isEnableSSL() {
		return enableSSL;
	}

	public void setEnableSSL(boolean enableSSL) {
		this.enableSSL = enableSSL;
	}

	public boolean isVerifySSLHost() {
		return verifySSLHost;
	}

	public void setVerifySSLHost(boolean verifySSLHost) {
		this.verifySSLHost = verifySSLHost;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	protected static class TrustAllManager implements X509TrustManager {

		private String host;
		private boolean verify = false;

		public TrustAllManager(String host, boolean verify) {
			this.host = host;
			this.verify = verify;
		}

		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			String name = "";
			if (arg0 != null && arg0.length > 0) {
				String dn = arg0[0].getSubjectDN().toString();
				int idx = dn.indexOf(",");
				if (idx == -1) {
					name = dn.substring(3);
				} else {
					name = dn.substring(3, idx);
				}
			}
			if (this.verify && this.host != null && !this.host.equals(name)) {
				throw new HttpException("Invalidate X509Certificate, hostname cannot be verfied.", 401);
			}
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

}
