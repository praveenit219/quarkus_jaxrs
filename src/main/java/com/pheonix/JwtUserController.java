package com.pheonix;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import com.pheonix.entity.JwtEntity;
import com.pheonix.exceptions.JwtProcessingException;
import com.pheonix.exceptions.JwtTokenExpiredException;
import com.pheonix.exceptions.JwtTokenInvalidException;
import com.pheonix.exceptions.JwtTokenNotFoundException;
import com.pheonix.pojo.ClockSkewRequest;
import com.pheonix.pojo.ClockSkewResponse;
import com.pheonix.pojo.JwtClaimsResponse;
import com.pheonix.pojo.JwtTokenResponse;
import com.pheonix.pojo.JwtUpdateRequest;
import com.pheonix.pojo.JwtUpdateResponse;
import com.pheonix.pojo.JwtVerificationRequest;
import com.pheonix.pojo.TokenRequest;
import com.pheonix.qualifiers.AdminApiAuthorizationCheck;
import com.pheonix.qualifiers.AuthorizationCheck;
import com.pheonix.services.ClockSkewService;
import com.pheonix.services.JwtUserRepository;
import com.pheonix.services.JwtUserService;
import com.pheonix.utils.StringUtils;



@Path("/api/identity")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JwtUserController {

	private static final Logger log = LoggerFactory.getLogger(JwtUserController.class);

	private JwtUserService jwtUserService;

	private ClockSkewService clockSkewService;

	private JwtUserRepository jwtRepositoryService;

	@Inject
	public JwtUserController(JwtUserService jwtUserService, ClockSkewService clockSkewService, JwtUserRepository jwtRepositoryService) {
		this.jwtUserService = jwtUserService;
		this.clockSkewService = clockSkewService;
		this.jwtRepositoryService = jwtRepositoryService;
	}


	@POST
	@AuthorizationCheck
	@Path("/jwt/token")
	public Response generateJwtTokenForCli(@Context UriInfo ui,@HeaderParam("Authorization") @NotBlank String authorization, @HeaderParam("date") @NotBlank String date, @Valid TokenRequest tokenRequest)  {
		log.info("http json api request for JWT - generating token");
		JwtTokenResponse jwtResponse = null;		
		JwtEntity jwtEntityInserted;
		String uri = ui.getPath();
		jwtResponse = processRequestforTokenGeneration(uri, tokenRequest);
		if(null!=jwtResponse) {
			jwtEntityInserted = jwtRepositoryService.saveJwtEntity(tokenRequest,jwtResponse);	
			if(null!=jwtEntityInserted && 1 == jwtEntityInserted.getStatus()) {
				jwtResponse.setStatus("active");
				return Response.ok(jwtResponse).build();
			}
		} else {
			log.error("cannot proceed, error during building jwt token");
			throw new JwtProcessingException("jwt processing exception during building jwt token");
		}		
		return Response.serverError().build();
	}

	@PUT
	@AuthorizationCheck
	@Path("/jwt/token")
	public Response updateJwtTokenForCli(@Context UriInfo ui,@HeaderParam("Authorization") @NotBlank String authorization, @HeaderParam("date") @NotBlank String date, @Valid JwtUpdateRequest tokenUpdateRequest )  {
		log.info("http json api request for JWT - updating token");
		JwtUpdateResponse jwtResponse = null;
		String uri = ui.getPath();
		jwtResponse = processTokenStatusUpdate(uri, tokenUpdateRequest);
		if(null!=jwtResponse)
			return Response.ok(jwtResponse).build();
		else {
			log.error("cannot proceed, error during building jwt token");
			throw new JwtProcessingException("jwt processing exception during building jwt token");
		}
	}

	
	@PUT
	@AuthorizationCheck
	@Path("/jwe/token")
	public Response updateJweTokenForCli(@Context UriInfo ui,@HeaderParam("Authorization") @NotBlank String authorization, @HeaderParam("date") @NotBlank String date, @Valid JwtUpdateRequest tokenUpdateRequest )  {
		log.info("http json api request for JWE - updating token");
		JwtUpdateResponse jwtResponse = null;
		String uri = ui.getPath();
		jwtResponse = processTokenStatusUpdate(uri, tokenUpdateRequest);
		if(null!=jwtResponse)
			return Response.ok(jwtResponse).build();
		else {
			log.error("cannot proceed, error during building jwt token");
			throw new JwtProcessingException("jwe processing exception during building jwt token");
		}
	}
	
	
	private JwtUpdateResponse processTokenStatusUpdate(String uri, @Valid JwtUpdateRequest tokenUpdateRequest) {
		JwtUpdateResponse jwtUpdateResponse = null;
		String jwtOrJwe = StringUtils.findPathForJWtorJWE(uri);
		if(log.isDebugEnabled())
			log.debug("processing deletion of token for {}", tokenUpdateRequest.getId());
		if(!StringUtils.isEmpty(jwtOrJwe)) {
			log.debug("modifying status of token in db");
			return jwtRepositoryService.updateTokenStatus(tokenUpdateRequest.getModifiedBy(),tokenUpdateRequest.getId());			
		}
		return jwtUpdateResponse; 
	}


	private  JwtClaimsResponse processRequestforTokenVerification(String uri, String token) {
		JwtClaimsResponse jwtResponse = null;		
		int status = -1;
		String jwtOrJwe = StringUtils.findPathForJWtorJWE(uri);
		if(log.isDebugEnabled())
			log.debug("verifying token for {}", jwtOrJwe);
		if(!StringUtils.isEmpty(jwtOrJwe)) {
			try {
				jwtResponse = jwtUserService.verifyClaimsOnly(token,jwtOrJwe);
				status = jwtRepositoryService.verifyJwtTokenStatus(token);
				if(log.isDebugEnabled())
					log.debug("status from db is {}", status);
				if(status == 1) {
					log.debug("verify the jwe status using the security mechanism");
					jwtResponse = jwtUserService.verifyClaimsOnly(token,jwtOrJwe);
				} else {
					log.error("token not found in db and returning error.");
					throw new JwtTokenNotFoundException("Token is not found or invalid from db check");
				}
				return jwtResponse;
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
		return null;
	}

	private  JwtTokenResponse processRequestforTokenGeneration(String uri, TokenRequest tokenRequest) {
		JwtTokenResponse jwtResponse = null;	
		String jwtOrJwe = StringUtils.findPathForJWtorJWE(uri);
		if(log.isDebugEnabled())
			log.debug("processing {} token for {}", jwtOrJwe, tokenRequest);
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
		JwtEntity jwtEntityInserted;
		String uri = ui.getPath();
		jwtResponse = processRequestforTokenGeneration(uri, tokenRequest);		
		if(null!=jwtResponse) {
			jwtEntityInserted = jwtRepositoryService.saveJwtEntity(tokenRequest,jwtResponse);	
			if(null!=jwtEntityInserted && 1 == jwtEntityInserted.getStatus()) {
				jwtResponse.setStatus("active");
				return Response.ok(jwtResponse).build();
			}
		} else {
			log.error("cannot proceed, error during building jwt token");
			throw new JwtProcessingException("jwe processing exception during building jwt token");
		}
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
