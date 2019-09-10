package com.pheonix.utils;


import java.util.List;

import javax.ws.rs.core.Response;

import com.pheonix.exceptions.ExceptionResponse;
import com.pheonix.exceptions.MoreInfo;

public class ExceptionUtils {

	private static final String REF_404NOTFOUND = "https://tools.ietf.org/html/rfc7231#section-6.5.4";
	private static final String REF_403FORBIDDEN = "https://tools.ietf.org/html/rfc7231#section-6.5.3";
	private static final String REF_500INTERNALSERVERERROR = "https://tools.ietf.org/html/rfc7231#section-6.6.1";
	private static final String REF_400BADREQUEST = "https://tools.ietf.org/html/rfc7231#section-6.5.1";
	private static final String REF_405NOTALLOWED = "https://tools.ietf.org/html/rfc7231#section-6.5.5";
	private static final String REF_405NOTACCEPTABLE = "https://tools.ietf.org/html/rfc7231#section-6.5.6";
	
	public static ExceptionResponse buildExceptionEntity(int statusCode, String details, String message, String statusRef, List<MoreInfo> moreInfoList) {
		ExceptionResponse error = new ExceptionResponse();
		error.setStatusCode(statusCode); 
		error.setDetails(details);
		error.setMessage(message);
		error.setStatusRef(statusRef);
		if(null!=moreInfoList && !moreInfoList.isEmpty()) {
			error.setMoreInfo(moreInfoList);
		}
		return error;
	}
	
	
	public static Response buildExceptionResponse(String message, int statusCode, List<MoreInfo> moreInfoList ) {
		
		switch (statusCode) {
		case 400:
			return Response.status(400).entity(buildExceptionEntity( 400, message, "BadRequest", REF_400BADREQUEST, (!moreInfoList.isEmpty())?moreInfoList:null))
	                .type("application/json").build();
		case 403:
			return Response.status(403).entity(buildExceptionEntity( 403, message, "Forbidden", REF_403FORBIDDEN, null))
	                .type("application/json").build();
		case 404:
			return Response.status(404).entity(buildExceptionEntity( 404, message, "NotFound", REF_404NOTFOUND, null))
	                .type("application/json").build();
		case 405:
			return Response.status(405).entity(buildExceptionEntity( 405, message, "NotAllowed", REF_405NOTALLOWED, null))
	                .type("application/json").build();
		case 406:
			return Response.status(406).entity(buildExceptionEntity( 406, message, "NotAcceptable", REF_405NOTACCEPTABLE, null))
	                .type("application/json").build();
		case 500:
			return Response.status(500).entity(buildExceptionEntity( 500, message, "InternalServerError", REF_500INTERNALSERVERERROR, null))
	                .type("application/json").build();

		default:
			break;
		}
		
		return null;
		
	}
}
