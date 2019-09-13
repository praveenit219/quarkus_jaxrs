package com.pheonix.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pheonix.pojo.JwtClaimsResponse;
import com.pheonix.pojo.JwtRequest;
import com.pheonix.pojo.JwtResponse;
import com.pheonix.pojo.JwtTokenResponse;
import com.pheonix.pojo.Services;
import com.pheonix.pojo.TokenRequest;
import com.pheonix.utils.StringUtils;



@Named
@Singleton
public class JwtUserService {

	private static final Logger log = LoggerFactory.getLogger(JwtUserService.class);


	JwtHandler jwtHandler;

	@Inject
	public JwtUserService(JwtHandler jwtHandler) {
		this.jwtHandler = jwtHandler;
	}


	private JwtTokenResponse buildJwtToken( TokenRequest tokenRequest, boolean jwe)  {			
		List<Services> eidServices = tokenRequest.getServices();	
		if(null!=eidServices && !eidServices.isEmpty()) {
			JwtRequest jwtRequest = new JwtRequest();
			buildJwtRequest(jwtRequest,tokenRequest);
			return jwtHandler.generateJwsSecureLogin(jwtRequest,jwe);
		}
		return null;
	}

	private void buildJwtRequest(JwtRequest jwtRequest,TokenRequest tokenRequest) {
		if(log.isDebugEnabled()) 
			log.debug("buildJwtRequest - JwtUserService, build token for specified options");
		List<Services> eidServices = tokenRequest.getServices();
		Services eidService = null;
		Map<String, Object> claims = new HashMap<>();	
		claims.put("_id", tokenRequest.getId());
		claims.put("scope", "admin/"+tokenRequest.geteServiceId());			
		if(null!=eidServices && !eidServices.isEmpty() ) {			
			Iterator<Services> eidServiceIter = eidServices.iterator();
			while (eidServiceIter.hasNext()) {
				eidService = eidServiceIter.next();
				claims.put("scope", "admin/"+tokenRequest.geteServiceId());
				if("sp".equals(eidService.getGateway())) {
					claims.put("sp", eidService.getGateway());
					claims.put("spCId", eidService.getPartnerId());
				}
				if("cp".equals(eidService.getGateway()) ) {
					claims.put("cp", eidService.getGateway());
					claims.put("cpCId", eidService.getPartnerId());
				}
			}		
		}

		jwtRequest.setClaims(claims);
		jwtRequest.setIssuer("PH-Identity-SERVER");
		jwtRequest.setSubject("PH IDENTITY Serivce");
		jwtRequest.setJwtExpiryInDays(tokenRequest.getExpiration());		
	}	



	public JwtTokenResponse buildJwt( TokenRequest tokenRequest, String jwtOrJwe)  {
		JwtTokenResponse jwtResponse = null;
		if( null!=tokenRequest) {			
			if("jwe".equals(jwtOrJwe)) 
				jwtResponse = buildJwtToken(tokenRequest,true);
			else 
				jwtResponse = buildJwtToken(tokenRequest,false);						
		}
		return jwtResponse;
	}


	public JwtResponse  verifyJweorJwt(String token, String jwtOrJwe) throws InvalidJwtException {
		if(log.isDebugEnabled()) 
			log.debug("verifyJweorJwt - JwtUserService, check jwt token for verification");
		JwtResponse jwtResponse = new JwtResponse(false, null);		
		JwtClaims jwtClaims = isJwtOrJwe(token,jwtOrJwe);
		if( null!=jwtClaims) 
			return new JwtResponse(true, jwtClaims);
		return jwtResponse;
	}


	private JwtClaims isJwtOrJwe(String token, String jwtOrJwe) throws InvalidJwtException {
		if(log.isDebugEnabled()) 
			log.debug("check if it is jwt or jwe from the request");
		JwtClaims jwtClaims = null;
		if(!StringUtils.isEmpty(token)) {
			if("jwe".equals(jwtOrJwe)) 
				jwtClaims = jwtHandler.parserJWEsecret(token, true);			
			else 
				jwtClaims = jwtHandler.parserJWEsecret(token, false);			
		}		
		return jwtClaims;
	}

	public JwtClaimsResponse verifyClaimsOnly(String token, String jwtOrJwe) throws MalformedClaimException, InvalidJwtException {
		if(log.isDebugEnabled()) 
			log.debug("check jwt token for verification and return only claims details");
		JwtClaims jwtClaims = isJwtOrJwe(token,jwtOrJwe);
		if( null!=jwtClaims) 
			return buildClaimsResponse( jwtClaims);
		return null;
	}

	private JwtClaimsResponse buildClaimsResponse(JwtClaims jwtClaims) throws MalformedClaimException {
		if(log.isDebugEnabled())
			log.debug("building cliams from the response");
		JwtClaimsResponse jwtResponse = new JwtClaimsResponse(false, null,null);
		HashMap claims = null; 
		String issuer = null;
		claims = (HashMap) jwtClaims.getClaimValue("claims");
		issuer = jwtClaims.getIssuer();
		if(null!=claims && !claims.isEmpty() && !StringUtils.isEmpty(issuer)) {
			jwtResponse = new JwtClaimsResponse(true, claims,issuer);
		}
		return jwtResponse;
	}

}
