package com.asksunny.tls;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

public class AliasSpecificKeyManager implements X509KeyManager {

	private X509KeyManager baseKM = null;
	private String alias = null;

	public AliasSpecificKeyManager(X509KeyManager keyManager, String alias) {
		baseKM = keyManager;
		this.alias = alias;
	}

	@Override
	public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
		return this.alias;
	}

	@Override
	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		return baseKM.chooseServerAlias(keyType, issuers, socket);
	}

	@Override
	public X509Certificate[] getCertificateChain(String arg0) {
		return baseKM.getCertificateChain(alias);
	}

	@Override
	public String[] getClientAliases(String keyType, Principal[] issuers) {
		return baseKM.getClientAliases(keyType, issuers);
	}

	@Override
	public PrivateKey getPrivateKey(String alias) {
		return baseKM.getPrivateKey(alias);
	}

	@Override
	public String[] getServerAliases(String keyType, Principal[] issuers) {
		return baseKM.getServerAliases(keyType, issuers);
	}

}
