package com.pheonix;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pheonix.exceptions.JwtProcessingException;
import com.pheonix.exceptions.JwtTokenExpiredException;
import com.pheonix.exceptions.JwtTokenInvalidException;
import com.pheonix.pojo.ClockSkewRequest;
import com.pheonix.pojo.ClockSkewResponse;
import com.pheonix.pojo.JwtClaimsResponse;
import com.pheonix.pojo.JwtTokenResponse;
import com.pheonix.pojo.JwtVerificationRequest;
import com.pheonix.pojo.TokenRequest;
import com.pheonix.qualifiers.AdminApiAuthorizationCheck;
import com.pheonix.qualifiers.AuthorizationCheck;
import com.pheonix.services.ClockSkewService;
import com.pheonix.services.JwtUserService;
import com.pheonix.utils.StringUtils;



@Path("/identity/saml")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JwtUserController {

	private static final Logger log = LoggerFactory.getLogger(JwtUserController.class);

	private JwtUserService jwtUserService;

	private ClockSkewService clockSkewService;

	@Inject
	public JwtUserController(JwtUserService jwtUserService, ClockSkewService clockSkewService) {
		this.jwtUserService = jwtUserService;
		this.clockSkewService = clockSkewService;
	}


	@POST
	@AuthorizationCheck
	@Path("/jwt/token")
	public Response generateJwtTokenForCli(@Context UriInfo ui,@HeaderParam("Authorization") @NotBlank String authorization, @HeaderParam("date") @NotBlank String date, @Valid TokenRequest tokenRequest)  {
		log.info("http json api request for JWT - generating token");
		JwtTokenResponse jwtResponse = null;		
		String uri = ui.getPath();
		jwtResponse = processRequestforTokenGeneration(uri, tokenRequest);		
		if(null!=jwtResponse) 					
			return Response.ok(jwtResponse).build();
		return Response.serverError().build();
	}

	private  JwtClaimsResponse processRequestforTokenVerification(String uri, String token) {
		JwtClaimsResponse jwtResponse = null;	
		String jwtOrJwe = StringUtils.findPathForJWtorJWE(uri);
		log.info("verifying token for {}", jwtOrJwe);
		if(!StringUtils.isEmpty(jwtOrJwe)) {
			try {
				jwtResponse = jwtUserService.verifyClaimsOnly(token,jwtOrJwe);
			} catch( MalformedClaimException e) {
				log.error("MalformedClaimException in partial may be cliams are not valid ", e);	
				throw new JwtTokenInvalidException("MalformedClaimException in cli may be cliams are not valid ", e);
			} catch (InvalidJwtException e) {	
				if(null!=e.getErrorDetails() && !e.getErrorDetails().isEmpty()) {
					if(null!=e.getErrorDetails().get(0).getErrorMessage() && e.getErrorDetails().get(0).getErrorMessage().contains("The JWT is no longer valid")) {
						log.error("token expired or jwt is no longer valid  ",e);
						throw new JwtTokenExpiredException("token expired or jwt is no longer valid", e);
					} else {
						log.error("InvalidJwtException in partial cannot proceed may be cliams are not valid or jwt is no longer valid  ",e);		
						throw new JwtTokenInvalidException("InvalidJwtException in parital may be cliams are not valid or jwt is no longer valid", e);
					}
				} else {
					log.error("InvalidJwtException in partial cannot proceed may be cliams are not valid or jwt is no longer valid  ",e);		
					throw new JwtTokenInvalidException("InvalidJwtException in parital may be cliams are not valid or jwt is no longer valid", e);
				}
			} catch (Exception e) {		
				log.error("issue with jwt processing ", e);			
				throw new JwtProcessingException("cannot process jwt verification or claims check", e);
			}
		}
		if(null!=jwtResponse) {				
			return jwtResponse;
		}
		return null;
	}

	private  JwtTokenResponse processRequestforTokenGeneration(String uri, TokenRequest tokenRequest) {
		JwtTokenResponse jwtResponse = null;	
		String jwtOrJwe = StringUtils.findPathForJWtorJWE(uri);
		log.info("processing {} token for {}", jwtOrJwe, tokenRequest);
		if(!StringUtils.isEmpty(jwtOrJwe)) {
			jwtResponse = jwtUserService.buildJwt(tokenRequest,jwtOrJwe);
			if(null!=jwtResponse) {				
				jwtResponse.setStatus("active");
				return jwtResponse;
			}
		}
		return null;
	}

	@POST
	@AuthorizationCheck
	@Path("/jwe/token")
	public Response generateJweTokenForCli(@Context UriInfo ui,@HeaderParam("Authorization") String authorization, @HeaderParam("date") String date, TokenRequest tokenRequest)  {
		log.info("http json api request for JWE - generating token");
		JwtTokenResponse jwtResponse = null;		
		String uri = ui.getPath();
		jwtResponse = processRequestforTokenGeneration(uri, tokenRequest);		
		if(null!=jwtResponse) 					
			return Response.ok(jwtResponse).build();
		return Response.serverError().build();
	}

	@POST
	@AuthorizationCheck
	@Path("/jwt/token/claims")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response generateJwtTokenVerificationForCli(@Context UriInfo ui,@HeaderParam("Authorization") String authorization, @HeaderParam("date") String date, JwtVerificationRequest tokenVerificationRequest)  {
		log.info("http json api request for JWT - verifying token");
		JwtClaimsResponse jwtResponse = null;		
		String uri = ui.getPath();
		jwtResponse = processRequestforTokenVerification(uri, tokenVerificationRequest.getToken());		
		if(null!=jwtResponse) 					
			return Response.ok(jwtResponse).build();
		return Response.serverError().build();
	}

	@POST
	@AuthorizationCheck
	@Path("/jwe/token/claims")
	public Response generateJweTokenVerificationForCli(@Context UriInfo ui,@HeaderParam("Authorization") String authorization, @HeaderParam("date") String date, JwtVerificationRequest tokenVerificationRequest)  {
		log.info("http json api request for JWE - verifying token");
		JwtClaimsResponse jwtResponse = null;		
		String uri = ui.getPath();
		jwtResponse = processRequestforTokenVerification(uri, tokenVerificationRequest.getToken());		
		if(null!=jwtResponse) 					
			return Response.ok(jwtResponse).build();
		return Response.serverError().build();
	}


	@POST
	@AdminApiAuthorizationCheck
	@Path("/token/clockskew")
	public Response generateClockSkew(ClockSkewRequest clockSkewRequest)  {
		log.info("http json api request clokSkew Request");
		String requestedUri = clockSkewRequest.getRequest();	
		ClockSkewResponse cskresponse = clockSkewService.processClockSkewSecretDetails(requestedUri);
		if(null!=cskresponse) 					
			return Response.ok(cskresponse).build();
		return Response.serverError().build();
	}
}
