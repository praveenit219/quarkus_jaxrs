package com.pheonix.services;

import java.text.SimpleDateFormat;
import java.time.Instant;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pheonix.configs.JsonWebKeyPairInitializer;
import com.pheonix.configs.JsonWebSecretKeyInitializer;
import com.pheonix.pojo.JwtRequest;
import com.pheonix.pojo.JwtTokenResponse;


@Named
@Singleton
public class JwtHandler {

	private static final Logger log = LoggerFactory.getLogger(JwtHandler.class);

	private static final String DATE_EXP_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final String CONTENT_TYPE_JWT_HEADER_VALUE = "JWT";

	private static final String TOKEN_ISSUER = "PH-Identity-SERVER";


	@Inject
	JsonWebKeyPairInitializer jsonWebKeyPair;

	@Inject
	JsonWebSecretKeyInitializer jsonWebKeyStatic;

	public JwtTokenResponse generateJwsSecureLogin(JwtRequest jwtRequest, boolean isJwe)  {
		if(log.isDebugEnabled()) 
			log.debug("generating claims for token");
		String status = "active";
		long exp = calculateExpTimeForToken(jwtRequest.getJwtExpiryInDays());
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_EXP_FORMAT); 
		String expiration  = sdf.format(exp);
		JwtClaims claims = new JwtClaims();
		buildClaims(jwtRequest, exp, claims);	
		JsonWebSignature jws = new JsonWebSignature();
		JsonWebEncryption jwe = new JsonWebEncryption();
		String jwt = null;
		String jweSerialization = null;
		if(isJwe) {
			jwt = buildJwsDetails(jws, claims, AlgorithmIdentifiers.RSA_USING_SHA256, true);
			jweSerialization = buildJweDetails(jwe, jwt, KeyManagementAlgorithmIdentifiers.RSA1_5, 
					ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256,true);	
		} else {
			jwt = buildJwsDetails(jws, claims,  AlgorithmIdentifiers.HMAC_SHA256,false);
			jweSerialization = buildJweDetails(jwe, jwt, KeyManagementAlgorithmIdentifiers.DIRECT, 
					ContentEncryptionAlgorithmIdentifiers.AES_192_CBC_HMAC_SHA_384, false);		
		}
		return new JwtTokenResponse(jweSerialization, expiration, status);
	}



	private void buildClaims(JwtRequest jwtRequest, long exp, JwtClaims claims) {
		if(log.isDebugEnabled()) 
			log.debug("building claism from request");
		claims.setIssuer(jwtRequest.getIssuer());		
		claims.setGeneratedJwtId();
		claims.setIssuedAtToNow();				
		claims.setExpirationTime(NumericDate.fromMilliseconds(exp));
		claims.setSubject(jwtRequest.getSubject());
		claims.setClaim("claims", jwtRequest.getClaims());

	}

	private long calculateExpTimeForToken(int days) {
		if(log.isDebugEnabled())
			log.debug("calculating exp time logic");
		long day = (1000 * 60 * 60 * 24); 	
		long timeinMillis = day * days;
		return Instant.now().plusMillis(timeinMillis).toEpochMilli();
	}

	private String buildJwsDetails(JsonWebSignature jws, JwtClaims claims, String alg, boolean isJwe ) {	
		if(log.isDebugEnabled())
			log.debug("building JWS logic");
		jws.setPayload(claims.toJson());
		if(isJwe) {
			jws.setKeyIdHeaderValue(jsonWebKeyPair.getJsonWebKey().getKeyId());
			jws.setKey(jsonWebKeyPair.getJsonWebKey().getKey());
		} else {
			jws.setKeyIdHeaderValue(jsonWebKeyStatic.getJsonWebKey().getKeyId());
			jws.setKey(jsonWebKeyStatic.getJsonWebKey().getKey());
		}
		jws.setAlgorithmHeaderValue(alg);
		String jwt = null;
		try {
			jwt = jws.getCompactSerialization();
		} catch(JoseException e) {
			log.error("error during jws serialization", e);
		}		
		return jwt;
	}

	private String buildJweDetails(JsonWebEncryption jwe,  String jwt, String headerAlg, String headerEnc, boolean isJwe) {
		if(log.isDebugEnabled()) 
			log.debug("building JWE logic");
		JsonWebKey jwKey = jsonWebKeyStatic.getJsonWebKey();
		if(isJwe) {
			jwe.setKey(jsonWebKeyPair.getKeyPair().getPublic());
			jwKey = jsonWebKeyPair.getJsonWebKey();
		} else 
			jwe.setKey(jwKey.getKey());

		jwe.setAlgorithmHeaderValue(headerAlg);
		jwe.setEncryptionMethodHeaderParameter(headerEnc);
		jwe.setKeyIdHeaderValue(jwKey.getKeyId());
		jwe.setContentTypeHeaderValue(CONTENT_TYPE_JWT_HEADER_VALUE);
		jwe.setPayload(jwt);
		String jweSerialization = null;
		try {
			jweSerialization = jwe.getCompactSerialization();
		} catch(JoseException e) {
			log.error("error during jwe serialization", e);
		}		
		return jweSerialization;
	}

	public JwtClaims parserJWEsecret(String jwt, boolean isJwe)throws InvalidJwtException {
		JwtConsumer jwtConsumer = null;		
		if(isJwe) {
			if(log.isDebugEnabled()) 
				log.debug("check jwe and parse claims with internal keypair");
			jwtConsumer = new JwtConsumerBuilder()				
					.setExpectedIssuer(TOKEN_ISSUER)				
					.setDecryptionKey(jsonWebKeyPair.getKeyPair().getPrivate())
					.setVerificationKey(jsonWebKeyPair.getKeyPair().getPublic()).build();
		} else {
			if(log.isDebugEnabled()) 
				log.debug("check jws and parse claims with static keys");
			jwtConsumer = new JwtConsumerBuilder()				
					.setExpectedIssuer(TOKEN_ISSUER)				
					.setDecryptionKey(jsonWebKeyStatic.getJsonWebKey().getKey())
					.setVerificationKey(jsonWebKeyStatic.getJsonWebKey().getKey()).build();
		}	
		return jwtConsumer.processToClaims(jwt);		
	}

}
