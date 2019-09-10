package com.pheonix.filters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pheonix.configs.AuthHeaderConfigs;
import com.pheonix.configs.KeyHashGenerator;
import com.pheonix.qualifiers.AuthorizationCheck;
import com.pheonix.utils.StringUtils;

@Provider
@AuthorizationCheck
public class AuthorizationCheckFilter implements ContainerRequestFilter {
	
	private static final Logger log = LoggerFactory.getLogger(AuthorizationCheckFilter.class);

	private static final  String[]  apiJwt = { "/identity/saml/jwt/token","/identity/saml/jwt/token/claims"} ;

	private static final  String[]  apiAll = { "/identity/saml/jwt/token", "/identity/saml/jwe/token",	
			"/identity/saml/jwt/token/claims",	"/identity/saml/jwe/token/claims"} ;
	
	@Inject
	KeyHashGenerator keyHashGenerator;

	@Inject
	AuthHeaderConfigs authHeaderConfigs;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		String dateHeader = requestContext.getHeaderString(HttpHeaders.DATE);		
		boolean isRequestValid = false;
		try {
			isRequestValid =	validateAuthorization(requestContext.getUriInfo().getPath(), dateHeader, authHeader, true );
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!isRequestValid) {
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN.getStatusCode(), "forbidden!").build());
		}
	}

	public boolean validateAuthorization(String uri, String generatedTime, String authorization, boolean isJweRequired) throws Exception {
		log.info("validate authorization with date and uri for the request");
		String randomSecret = "FNgLY+f.N{&M;/jp`J$X<<.e/lF[<C)r9(-[DT!LsPWmrMBZL7_@&<^N|zx9l?&";

		log.info("uri calling is {}", uri);
		boolean validAuth = false;
		boolean hashMatched = false;
		boolean isClockSkewCheck = authHeaderConfigs.isClockSkewDifference();
		if(log.isDebugEnabled())
			log.debug("authorization received in header is {}",authorization);
		boolean matchAPIRequest =  verifyApiRequestMatch(uri, isJweRequired);
		
		if(!matchAPIRequest) 			
			log.info("requests are not matching with the allowed URIs, please check the url");
		
		if(matchAPIRequest && !StringUtils.isEmpty(authorization)) {
			hashMatched = hashMatchStatus(uri, randomSecret, authorization);			
		}
		if(isClockSkewCheck && hashMatched && !StringUtils.isEmpty(generatedTime)) {
			validAuth = findMilliDifferences(generatedTime);
			if(log.isDebugEnabled())
				log.debug("validAuthorization status is {}",validAuth);
		} else if(!isClockSkewCheck && hashMatched) {
			validAuth = true;
			if(log.isDebugEnabled())
				log.debug("clockskewcheck is disabled and validAuthorization status is {}",validAuth);
		}
		if(!validAuth) {			
			throw new Exception("Authorization validation issue with the token. please check");
		}
		log.info("validate authorization with date and uri for the request return with {}", validAuth);
		return validAuth;
	}

	private boolean hashMatchStatus(String uri, String randomSecret, String authorization) {
		if(log.isDebugEnabled())
			log.debug("verify if hash is same for both the uris");
		StringBuilder message = new StringBuilder();
		String hashedIs = null; 
		boolean hashMatched = false;
		message.append(uri);		
		if(null!=keyHashGenerator && !keyHashGenerator.getApiKeyHash().isEmpty()) {
			hashedIs = keyHashGenerator.getApiKeyHash().get(uri);
			if(log.isDebugEnabled())
				log.debug("found Hashed value from cache {}",hashedIs);
		} else {
			hashedIs = encryptHash(message.toString(),randomSecret);
			if(log.isDebugEnabled())
				log.debug("generated hashed value {}",hashedIs);
		}
		String secret = authorization.substring(authorization.indexOf(' ')+1);
		if(log.isDebugEnabled())
			log.debug("authorization received is {}",secret);
		if(!StringUtils.isEmpty(secret)) {
			hashMatched = secret.equals(hashedIs);
			if(log.isDebugEnabled())
				log.debug("compared hash status {}",hashMatched);
		}
		return hashMatched;
	}


	private boolean verifyApiRequestMatch(String requestURI, boolean isJweRequired) {
		if(isJweRequired)
			return Arrays.stream(apiAll).anyMatch(requestURI::equals);
		else 
			return Arrays.stream(apiJwt).anyMatch(requestURI::equals);		
	}

	private boolean findMilliDifferences(String generatedTime) {				
		if(log.isDebugEnabled())
			log.debug("clockskewcheck is enabled find the difference of request origin date vs current date");
		Instant requestedTime = Instant.ofEpochMilli(Long.parseLong(generatedTime));
		Instant current = Instant.now();				
		Duration difference = Duration.between(requestedTime,current);
		if(log.isDebugEnabled()) {
			log.debug("date received in header is {}",generatedTime);
			log.debug("requestedTime epoc millis is {}",requestedTime);
			log.debug("current epoc millis is {}",current);
			log.debug("difference of instants is {}",difference.getSeconds());
		}
		if(difference.isNegative()) {
			if(log.isDebugEnabled()) 
				log.debug("APISecretVerificationFilter, validAuthorization status is false due to difference in negative");
			return false;
		}
		if(difference.getSeconds()< authHeaderConfigs.getClockSkew()) {		
			if(log.isDebugEnabled()) 
				log.debug("APISecretVerificationFilter, validAuthorization status is true");
			return true;
		}		
		return false;		
	}

	private  String encryptHash(String message, String key) {

		Mac sha256Hmac;
		byte[] finalval = null;
		if(!StringUtils.isEmpty(message) && !StringUtils.isEmpty(key)) {
			try {
				sha256Hmac = Mac.getInstance("HmacSHA256");
				SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
				sha256Hmac.init(secretKey);
				finalval = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
				return Base64.getEncoder().encodeToString(finalval);
			} catch (NoSuchAlgorithmException | InvalidKeyException e) {
				log.error("issue during encrypting hash", e);
			}		
		}
		return null;
	}
}
