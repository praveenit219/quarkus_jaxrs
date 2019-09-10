package com.pheonix.filters;

import java.io.IOException;
import java.util.Base64;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pheonix.qualifiers.AdminApiAuthorizationCheck;

@Provider
@PreMatching
@AdminApiAuthorizationCheck
public class AdminAuthorizationCheckFilter implements ContainerRequestFilter {
	
	private static final Logger log = LoggerFactory.getLogger(AdminAuthorizationCheckFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		log.info("checking the authorization for the admin API");
		String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		byte[] decodedAuthValue = Base64.getDecoder().decode(authHeader.substring(authHeader.indexOf(' ')+1));
		String[] cred = decodedAuthValue.toString().split(":");
		boolean allowed = false;
		if(cred.length==2) {
			if(cred[0].equals("admin") && cred[1].equals("YWRtaW46cGFzc3dvcmQkMQ==")) {
				log.info("cred match for admin API");
				allowed = true;
			}
		}
		if(!allowed) {
			requestContext.abortWith(Response.accepted("forbidden!").build());
		}
	}

}
