package com.pheonix.services;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pheonix.pojo.ClockSkewResponse;

@Named
@Singleton
public class ClockSkewService {

	private static final Logger log = LoggerFactory.getLogger(ClockSkewService.class);


	public ClockSkewResponse processClockSkewSecretDetails(String uri) {
		String secret = "FNgLY+f.N{&M;/jp`J$X<<.e/lF[<C)r9(-[DT!LsPWmrMBZL7_@&<^N|zx9l?&";
		ClockSkewResponse clockSkewResponse = null;
		try {			
			if(log.isDebugEnabled()) 
				log.debug("encyrpting hash for message {}",uri);
			String encryptMac = encryptHash(uri, secret);
			Instant instant = Instant.now();
			long epochMilli = instant.toEpochMilli();
			if(log.isDebugEnabled()) 
				log.debug("calculated clockskew response Hash {} and millis {}", encryptMac, epochMilli);
			clockSkewResponse = new ClockSkewResponse();
			clockSkewResponse.setAuthorization("Bearer "+ encryptMac);
			clockSkewResponse.setUri(uri);
			clockSkewResponse.setDate(epochMilli);
			clockSkewResponse.setValidity("2-minutes");
			return clockSkewResponse;

		} catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			log.error("issue during hashing",e);
		}
		return null;

	}

	private String encryptHash(String message, String key) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		Mac sha256_hmac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
		sha256_hmac.init(secret_key);
		String hash = Base64.getEncoder().encodeToString(sha256_hmac.doFinal(message.getBytes("UTF-8")));
		return hash;

	}


}
