package com.pheonix.filters;

import java.io.IOException;
import java.util.Base64;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pheonix.qualifiers.AdminApiAuthorizationCheck;

@Provider
@AdminApiAuthorizationCheck
public class AdminAuthorizationCheckFilter implements ContainerRequestFilter {
	
	private static final Logger log = LoggerFactory.getLogger(AdminAuthorizationCheckFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		log.info("checking the authorization for the admin API");
		String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		log.info("authHeader {}", authHeader);
		String[] authParts = authHeader.split("\\s+");
        String authInfo = authParts[1];
        // Decode the data back to original string
        byte[] bytes = Base64.getDecoder().decode(authInfo);        
        String decodedAuthValue = new String(bytes);
        log.info("decodedAuthValue {}", decodedAuthValue);
		String[] cred = decodedAuthValue.toString().split(":");
		log.info("cred {} {}", cred, cred.length);
		boolean allowed = false;
		if(cred.length==2) {
			if(cred[0].equals("admin") && cred[1].equals("YWRtaW46cGFzc3dvcmQkMQ==")) {
				log.info("cred match for admin API");
				allowed = true;
			}
		}
		if(!allowed) {
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN.getStatusCode(), "forbidden!").build());
		}
	}

}
