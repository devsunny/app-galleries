package com.asksunny.codegen;

import java.util.Date;

import com.asksunny.schema.Schema;

public class SSLCertificateGenerator extends CodeGenerator {

	
	
	public static final String SIGNATURE_ALGORITHM = "SHA256WithRSAEncryption";  
	public static final String KEY_GENERATION_ALGORITHM = "RSA";  
	public static final boolean REGENERATE_FRESH_CA_CERTIFICATE = false; 
	public static final int ROOT_KEYSIZE = 2048; 
	/**
     * Current time minus 1 year
     */ 
	public static final Date NOT_BEFORE = new Date(System.currentTimeMillis() - 86400000L * 365); 
 
    /**    
     * Hundred years in the future. 
     */ 
	public static final Date NOT_AFTER = new Date(System.currentTimeMillis() + 86400000L * 365 * 100); 
	
	public SSLCertificateGenerator(CodeGenConfig configuration, Schema schema) {
		super(configuration, schema);
	}

	public void doCodeGen() {
		
		//X509v3CertificateBuilder v3CertGen = new X509v3CertificateBuilder(arg0, arg1, arg2, arg3, arg4, arg5)

	}
	
	
	

	
}
