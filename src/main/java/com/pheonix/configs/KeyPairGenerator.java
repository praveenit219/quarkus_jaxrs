package com.pheonix.configs;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pheonix.utils.CredentialsDecryption;



@Named
@Singleton
public class KeyPairGenerator {

	private static final Logger log = LoggerFactory.getLogger(KeyPairGenerator.class);

	private SecretKeyGenerator keyInitializer;
	private CredentialsDecryption credentialDecryption;

	public KeyPairGenerator() {}

	@Inject
	public KeyPairGenerator(SecretKeyGenerator keyInitializer, CredentialsDecryption credentialDecryption) {
		this.keyInitializer = keyInitializer;
		this.credentialDecryption = credentialDecryption;
	}


	@Inject
	KeyStoreConfiguration keyStoreConfiguration;


	private KeyPair keyPair;


	public synchronized KeyPair getKeyPair() {
		return keyPair;
	}

	public synchronized void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	protected InputStream readProtectedResorce(){
		return getClass()
				.getClassLoader().getResourceAsStream("samljwt.jks");
	}


	protected void prepareKeyPair(String jwtKeyStore, String jwtKeyStorePwd, String jwtKeyStoreAlias,
			boolean jwtKeyStoreEncrypted) {
		if(log.isDebugEnabled()) 
			log.debug("preparing keypair to set using keystore details");
		KeyStore keystore;
		if(null!=keyInitializer) {
			if(log.isDebugEnabled()) 
				log.debug("keyInitializer.getSensitiveDataChecksum() {}", keyInitializer.getSensitiveDataChecksum());
		}
		try {
			if(jwtKeyStoreEncrypted) {
				if(log.isDebugEnabled()) 
					log.debug("decrypt using keyinitializer checksum is {}", keyInitializer.getSensitiveDataChecksum());				
				jwtKeyStorePwd = credentialDecryption.decrypt(keyInitializer.getSensitiveDataChecksum(),jwtKeyStorePwd);
				if(log.isDebugEnabled()) 
					log.debug("key passwrod decryped is {}", jwtKeyStorePwd);
			}
			if(log.isDebugEnabled()) 
				log.debug(" proceeding to initialize the keys using the password");
			keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(readProtectedResorce(), jwtKeyStorePwd.toCharArray());
			setKeyPair(generatePrivateKey(findKeyFromKeystoreusingAlias(keystore, jwtKeyStoreAlias, jwtKeyStorePwd), keystore, jwtKeyStoreAlias));
		} catch (KeyStoreException | NoSuchAlgorithmException e) {
			log.error("keypair exception issue for keystore ", e);
		} catch (CertificateException e) {
			log.error("keypair certifcate issue for keystore ", e);
		} catch (IOException e) {
			log.error("keypair ioexception issue for keystore ", e);
		} catch (UnrecoverableKeyException e) {
			log.error("keypair unrecoverable key issue for keystore ", e);
		}

	}

	private Key findKeyFromKeystoreusingAlias(KeyStore keystore,String jwtKeyStoreAlias,  String jwtKeyStoreAliasPwd) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		return keystore.getKey(jwtKeyStoreAlias, jwtKeyStoreAliasPwd.toCharArray());
	}

	private KeyPair generatePrivateKey(Key key, KeyStore keystore, String jwtKeyStoreAlias) throws KeyStoreException {
		if (key instanceof PrivateKey) {		      
			Certificate cert = keystore.getCertificate(jwtKeyStoreAlias);
			PublicKey publicKey = cert.getPublicKey();
			if(log.isDebugEnabled()) 
				log.debug("private Key generated..");
			return new KeyPair(publicKey, (PrivateKey) key);
		}
		return null;
	}


	void loadKeys() {
		if(log.isDebugEnabled()) 
			log.debug("calling postconstruct to resolve details..");
		if(keyStoreConfiguration.isEnable())
			prepareKeyPair(keyStoreConfiguration.getLocation(), keyStoreConfiguration.getPwd(), keyStoreConfiguration.getAlias(),
					keyStoreConfiguration.isEncrypted());
	}
}
