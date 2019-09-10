package com.pheonix.exceptions;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pheonix.utils.ExceptionUtils;

@Provider
public class InternalServerErrorExceptionMapper implements ExceptionMapper<InternalServerErrorException> {
	
	
	
	public InternalServerErrorExceptionMapper() {}


	@Override
	public Response toResponse(InternalServerErrorException exception) {
		return ExceptionUtils.buildExceptionResponse(exception.getLocalizedMessage(), 500, null);
	}

}
