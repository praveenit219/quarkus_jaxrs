package com.pheonix.exceptions;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pheonix.utils.ExceptionUtils;

@Provider
public class NotAcceptableExceptionMapper implements ExceptionMapper<NotAcceptableException> {

	public NotAcceptableExceptionMapper() {}
	
	@Override
	public Response toResponse(NotAcceptableException exception) {
		return ExceptionUtils.buildExceptionResponse(exception.getLocalizedMessage(), 406, null);
	}
	

}
