package com.pheonix.configs;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pheonix.utils.StringUtils;


@Named
@Singleton
public class KeyHashGenerator {

	private static final Logger log = LoggerFactory.getLogger(KeyHashGenerator.class);

	private static final  String[]  api = { "/api/identity/jwt/token",
			"/api/identity/jwe/token",
			"/api/identity/jwt/token/claims",
	"/api/identity/jwe/token/claims"} ;

	private  Map<String,String> apiKeyHash = new HashMap<>();

	public Map<String, String> getApiKeyHash() {
		return apiKeyHash;
	}


	private static final String S_S_K = "FNgLY+f.N{&M;/jp`J$X<<.e/lF[<C)r9(-[DT!LsPWmrMBZL7_@&<^N|zx9l?&";

	public  String encryptHash(String message, String key)  {

		Mac sha256Hmac;
		byte[] finalDat = null;
		if(!StringUtils.isEmpty(message) && !StringUtils.isEmpty(key)) {
			try {
				sha256Hmac = Mac.getInstance("HmacSHA256");
				SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
				sha256Hmac.init(secretKey);
				finalDat = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
				return Base64.getEncoder().encodeToString(finalDat);
			} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException e) {
				log.error("exception during hash generation for keys", e);
			}
		}		
		return null;
	}

	@PostConstruct
	void hashGenerator() {		

		String hashed = null;
		for(int i=0;i<api.length;i++) {
			hashed = encryptHash(api[i],S_S_K);
			apiKeyHash.put(api[i], hashed);
		}
		if(log.isDebugEnabled())
			log.debug("hash Values generated and stored in hashmap internal cache");
	}

}
