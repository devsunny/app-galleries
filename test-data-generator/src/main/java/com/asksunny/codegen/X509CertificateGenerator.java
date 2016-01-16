package com.asksunny.codegen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.RSAPublicKeyStructure;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jce.provider.JDKDigestSignature.SHA256WithRSAEncryption;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.util.encoders.Hex;

/** This class uses the Bouncycastle lightweight API to generate X.509 certificates programmatically.
 * It assumes a CA certificate and its private key to be available and can sign the new certificate with
 * this CA. Some of the code for this class was taken from 
 * org.bouncycastle.x509.X509V3CertificateGenerator, but adapted to work with the lightweight API instead of
 * JCE (which is usually not available on MIDP2.0). 
 * 
 * @author Rene Mayrhofer
 */
public class X509CertificateGenerator {
    /** Our log4j logger. */
    private static Logger logger = Logger.getLogger(X509CertificateGenerator.class.getName());

    /** This holds the certificate of the CA used to sign the new certificate. The object is created in the constructor. */
    private X509Certificate caCert;
    /** This holds the private key of the CA used to sign the new certificate. The object is created in the constructor. */
    private RSAPrivateCrtKeyParameters caPrivateKey;

    private boolean useBCAPI;
    private boolean useCACert;

    public X509CertificateGenerator(String caFile, String caPassword, String caAlias, boolean useBCAPI) 
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.useBCAPI = useBCAPI;
        this.useCACert = true;
        logger.info("Loading CA certificate and private key from file '" + caFile + "', using alias '" + caAlias + "' with "
                + (this.useBCAPI ? "Bouncycastle lightweight API" : "JCE API"));
        KeyStore caKs = KeyStore.getInstance("PKCS12");
        caKs.load(new FileInputStream(new File(caFile)), caPassword.toCharArray());

        // load the key entry from the keystore
        Key key = caKs.getKey(caAlias, caPassword.toCharArray());
        if (key == null) {
            throw new RuntimeException("Got null key from keystore!"); 
        }
        RSAPrivateCrtKey privKey = (RSAPrivateCrtKey) key;
        caPrivateKey = new RSAPrivateCrtKeyParameters(privKey.getModulus(), privKey.getPublicExponent(), privKey.getPrivateExponent(),
                privKey.getPrimeP(), privKey.getPrimeQ(), privKey.getPrimeExponentP(), privKey.getPrimeExponentQ(), privKey.getCrtCoefficient());
        // and get the certificate
        caCert = (X509Certificate) caKs.getCertificate(caAlias);
        if (caCert == null) {
            throw new RuntimeException("Got null cert from keystore!"); 
        }
        logger.info("Successfully loaded CA key and certificate. CA DN is '" + caCert.getSubjectDN().getName() + "'");
        caCert.verify(caCert.getPublicKey());
        logger.info("Successfully verified CA certificate with its own public key.");
    }
    public X509CertificateGenerator(boolean useBCAPI) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.useBCAPI = useBCAPI;
        this.useCACert = false;
    }

    public X509Certificate createCertificate(String dn, int validityDays, X500Principal issuer, KeyPair keypairca) throws 
            IOException, InvalidKeyException, SecurityException, SignatureException, NoSuchAlgorithmException, DataLengthException, CryptoException, KeyStoreException, NoSuchProviderException, CertificateException, InvalidKeySpecException {
        logger.info("Generating certificate for distinguished subject name '" + 
                dn + "', valid for " + validityDays + " days");

        RSAPrivateCrtKey pK = (RSAPrivateCrtKey) keypairca.getPrivate();
        caPrivateKey = new RSAPrivateCrtKeyParameters(pK.getModulus(), pK.getPublicExponent(), pK.getPrivateExponent(),
                pK.getPrimeP(), pK.getPrimeQ(), pK.getPrimeExponentP(), pK.getPrimeExponentQ(), pK.getCrtCoefficient());

        SecureRandom sr = new SecureRandom();

        PublicKey pubKey;
        PrivateKey privKey;

        logger.info("Creating RSA keypair");
        // generate the keypair for the new certificate
        if (useBCAPI) {
            RSAKeyPairGenerator gen = new RSAKeyPairGenerator();
            gen.init(new RSAKeyGenerationParameters(BigInteger.valueOf(3), sr, 1024, 80));
            AsymmetricCipherKeyPair keypair = gen.generateKeyPair();
            logger.info("Generated keypair, extracting components and creating public structure for certificate");
            RSAKeyParameters publicKey = (RSAKeyParameters) keypair.getPublic();
            RSAPrivateCrtKeyParameters privateKey = (RSAPrivateCrtKeyParameters) keypair.getPrivate();
            // used to get proper encoding for the certificate
            RSAPublicKey pkStruct = new org.bouncycastle.asn1.pkcs.RSAPublicKey(publicKey.getModulus(), publicKey.getExponent());
            
          
            pubKey = KeyFactory.getInstance("RSA").generatePublic(
                    new RSAPublicKeySpec(publicKey.getModulus(), publicKey.getExponent()));            
            privKey = KeyFactory.getInstance("RSA").generatePrivate(
                    new RSAPrivateCrtKeySpec(publicKey.getModulus(), publicKey.getExponent(),
                            privateKey.getExponent(), privateKey.getP(), privateKey.getQ(), 
                            privateKey.getDP(), privateKey.getDQ(), privateKey.getQInv()));
        }
        else {
            // this is the JSSE way of key generation
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024, sr);
            KeyPair keypair = keyGen.generateKeyPair();
            privKey = keypair.getPrivate();
            pubKey = keypair.getPublic();
        }

        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.DAY_OF_YEAR, validityDays);

        org.bouncycastle.asn1.x500.X500Name x509Name = new org.bouncycastle.asn1.x500.X500Name("CN=" + dn);
        org.bouncycastle.asn1.x500.X500Name x509Issuer = new org.bouncycastle.asn1.x500.X500Name(issuer.getName());
        V3TBSCertificateGenerator certGen = new V3TBSCertificateGenerator();
        certGen.setSerialNumber(new ASN1Integer(BigInteger.valueOf(System.currentTimeMillis())));
        certGen.setIssuer(x509Issuer);//issuer.getName());//PrincipalUtil.getSubjectX509Principal(caCert));
        certGen.setSubject(x509Name);        
        ASN1ObjectIdentifier sigOID = PKCSObjectIdentifiers.sha256WithRSAEncryption;       
        AlgorithmIdentifier sigAlgId = new AlgorithmIdentifier(sigOID, DERNull.INSTANCE);
        certGen.setSignature(sigAlgId);
        ASN1InputStream asn1in = new ASN1InputStream(new ByteArrayInputStream(pubKey.getEncoded()));
        certGen.setSubjectPublicKeyInfo(new SubjectPublicKeyInfo((ASN1Sequence)asn1in.readObject()));
        asn1in.close();
        certGen.setStartDate(new Time(new Date(System.currentTimeMillis())));
        certGen.setEndDate(new Time(expiry.getTime()));
        logger.info("Certificate structure generated, creating SHA1 digest");
       
        SHA1Digest digester = new SHA1Digest();
        AsymmetricBlockCipher rsa = new PKCS1Encoding(new RSAEngine());
        TBSCertificate tbsCert = certGen.generateTBSCertificate();

        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        DEROutputStream         dOut = new DEROutputStream(bOut);
        dOut.writeObject(tbsCert);

        // and now sign
        byte[] signature;
        if (useBCAPI) {
            byte[] certBlock = bOut.toByteArray();
            // first create digest            
            digester.update(certBlock, 0, certBlock.length);
            byte[] hash = new byte[digester.getDigestSize()];
            digester.doFinal(hash, 0);
            // and sign that
            rsa.init(true, caPrivateKey);
            DigestInfo dInfo = new DigestInfo( new AlgorithmIdentifier(X509ObjectIdentifiers.id_SHA1, null), hash);
            byte[] digest = dInfo.getEncoded(ASN1Encoding.DER);
            signature = rsa.processBlock(digest, 0, digest.length);
        }
        else {
            // or the JCE way
            PrivateKey caPrivKey = KeyFactory.getInstance("RSA").generatePrivate(
                    new RSAPrivateCrtKeySpec(caPrivateKey.getModulus(), caPrivateKey.getPublicExponent(),
                            caPrivateKey.getExponent(), caPrivateKey.getP(), caPrivateKey.getQ(), 
                            caPrivateKey.getDP(), caPrivateKey.getDQ(), caPrivateKey.getQInv()));

            Signature sig = Signature.getInstance(sigOID.getId());
            sig.initSign(caPrivKey, sr);
            sig.update(bOut.toByteArray());
            signature = sig.sign();
        }
      
        // and finally construct the certificate structure
        ASN1EncodableVector  v = new ASN1EncodableVector();

        v.add(tbsCert);
        v.add(sigAlgId);
        v.add(new DERBitString(signature));

        X509CertificateObject clientCert = new X509CertificateObject(org.bouncycastle.asn1.x509.Certificate.getInstance(new DERSequence(v))); 
        logger.info("Verifying certificate for correct signature with CA public key");

        if(useCACert) clientCert.verify(caCert.getPublicKey());

        return clientCert;
    }


}
