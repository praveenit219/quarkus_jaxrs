package com.pheonix.configs;

import java.security.KeyPair;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKey.Factory;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Singleton
public class JsonWebKeyPairInitializer {

	private static final Logger log = LoggerFactory.getLogger(JsonWebKeyPairInitializer.class);

	private KeyPairGenerator keyPairGenerator;
	
	private JsonWebKey jsonWebKey;

	public void setJsonWebKey(JsonWebKey jsonWebKey) {
		this.jsonWebKey = jsonWebKey;
	}
	

	public JsonWebKey getJsonWebKey() {
		return jsonWebKey;
	}

	@Inject
	public JsonWebKeyPairInitializer(KeyPairGenerator keyPairGenerator) {
		this.keyPairGenerator = keyPairGenerator;
	}
	

	public KeyPair getKeyPair() {
		if(null!=keyPairGenerator && null!=keyPairGenerator.getKeyPair())
			return keyPairGenerator.getKeyPair();
		return null;
	}

	void loadJsonWebKeys() {
		log.info("initializing of  jwonwebkey factory using privatekey");
		JsonWebKey jwKey = null;
		try {
			jwKey = Factory.newJwk(keyPairGenerator.getKeyPair().getPrivate());
		} catch (JoseException e1) {			
			log.error("exception during jwonwebkey factory initialiazation of privatekey");
		}		
		setJsonWebKey(jwKey);
	}

}
