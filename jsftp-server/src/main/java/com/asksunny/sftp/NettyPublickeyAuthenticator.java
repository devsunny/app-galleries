package com.asksunny.sftp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

import org.apache.mina.util.Base64;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class NettyPublickeyAuthenticator implements PublickeyAuthenticator {

	private static final String knownKey = "{SSH2.PUBLIC.KEY}";
	
	public NettyPublickeyAuthenticator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean authenticate(String username, PublicKey key,
			ServerSession session) {
		if (key instanceof RSAPublicKey) {
			String s1 = new String(encode((RSAPublicKey) key));
			String s2 = new String(Base64.decodeBase64(knownKey.getBytes()));
			return s1.equals(s2); // Returns true if the key matches our known
									// key, this allows auth to proceed.
		}
		return false; // Doesn't handle other key types currently.
	}

	public static byte[] encode(RSAPublicKey key) {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			byte[] name = "ssh-rsa".getBytes("US-ASCII");
			write(name, buf);
			write(key.getPublicExponent().toByteArray(), buf);
			write(key.getModulus().toByteArray(), buf);
			return buf.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void write(byte[] str, OutputStream os) throws IOException {
		for (int shift = 24; shift >= 0; shift -= 8)
			os.write((str.length >>> shift) & 0xFF);
		os.write(str);
	}

}
