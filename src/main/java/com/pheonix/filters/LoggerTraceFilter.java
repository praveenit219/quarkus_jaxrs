package com.pheonix.filters;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.MDC;

import com.pheonix.utils.StringUtils;

@Provider
@Priority(1)
public class LoggerTraceFilter implements ContainerRequestFilter , ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String correlationId = replaceAscii(requestContext.getHeaderString("ph-correlationId"));
		MDC.put("traceId", correlationId);

	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		responseContext.getHeaders().add("ph-correlationId", MDC.get("traceId"));
		MDC.remove("traceId");

	}

	private String replaceAscii(String headerToken) {
		if(StringUtils.isEmpty(headerToken)) {
			return UUID.randomUUID().toString();
		}
		return headerToken.replaceAll("\\r\\n|\\r|\\n", "");
	}

}
