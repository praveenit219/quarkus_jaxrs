package com.pheonix.exceptions;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pheonix.utils.ExceptionUtils;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
	
	
	
	public BadRequestExceptionMapper() {}


	@Override
	public Response toResponse(BadRequestException exception) {
		
		return ExceptionUtils.buildExceptionResponse(exception.getLocalizedMessage(), 400, null);
	}


}
