package com.pheonix.configs;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKey.Factory;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pheonix.utils.StringUtils;

@Named
@Singleton
public class JsonWebSecretKeyInitializer {

	private static final Logger log = LoggerFactory.getLogger(JsonWebSecretKeyInitializer.class);
	
	private SecretKeyGenerator generatedSecreyKey;
	
	private JsonWebKey jsonWebKey;
	
	
	
	public JsonWebKey getJsonWebKey() {
		return jsonWebKey;
	}


	public void setJsonWebKey(JsonWebKey jsonWebKey) {
		this.jsonWebKey = jsonWebKey;
	}


	@Inject
    public JsonWebSecretKeyInitializer(SecretKeyGenerator generatedSecreyKey) {
        this.generatedSecreyKey = generatedSecreyKey;
    }
	

	public void loadJsonWebKeys() {
		log.info("initialization of  jwonwebkey factory using staticKey");
		JsonWebKey jwKey = null;
		String jwtJson = null;
		String jwtJsonkey = generatedSecreyKey.getSensitiveDataChecksum();
		if(StringUtils.isEmpty(jwtJsonkey)) {
			jwtJson = "{\"kty\":\"oct\",\"k\":\"9d6722d6-b45c-4dcb-bd73-2e057c44eb93-928390\"}";
		} else {
			jwtJson = "{\"kty\":\"oct\",\"k\":\""+jwtJsonkey+"\"}";
		}
		try {				
			jwKey = Factory.newJwk(jwtJson);		
		} catch (JoseException e) {		
			log.error("exception during jwonwebkey factory initialiazation of statickey");	
		}		
		setJsonWebKey(jwKey);
	}
}
