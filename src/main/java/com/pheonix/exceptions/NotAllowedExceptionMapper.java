package com.pheonix.exceptions;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pheonix.utils.ExceptionUtils;

@Provider
public class NotAllowedExceptionMapper implements ExceptionMapper<NotAllowedException> {
	
	
	
	public NotAllowedExceptionMapper() {}


	@Override
	public Response toResponse(NotAllowedException exception) {
		return ExceptionUtils.buildExceptionResponse(exception.getLocalizedMessage(), 405, null);
	}

}
