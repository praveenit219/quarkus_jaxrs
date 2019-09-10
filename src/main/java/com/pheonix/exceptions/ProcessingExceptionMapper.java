package com.pheonix.exceptions;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pheonix.utils.ExceptionUtils;

@Provider
public class ProcessingExceptionMapper implements ExceptionMapper<ProcessingException> {

	@Override
	public Response toResponse(ProcessingException exception) {
		return ExceptionUtils.buildExceptionResponse(exception.getLocalizedMessage(), 500, null);
	}
	

}
